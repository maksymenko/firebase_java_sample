package com.sm.firebasequeue;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class EventHandlerTest {
  @Mock
  private DataSnapshot dataSnapshotMock;
  @Mock
  private DatabaseError databaseError;
  @Mock
  private EventListener eventListenerMock;
  @Captor
  private ArgumentCaptor<DataSnapshot> datasnapshotCaptor;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void shouldCallHandlerforAddChildEvent() throws Exception {
    EventHandler eventHandler = new EventHandler(eventListenerMock);
    eventHandler.onChildAdded(dataSnapshotMock, "previousChildKey");

    verify(eventListenerMock).handle(dataSnapshotMock);
  }

  @Test
  public void shouldNotCallListenerForOtherMethodThenAddChild() {
    EventHandler eventHandler = new EventHandler(eventListenerMock);
    eventHandler.onCancelled(databaseError);
    eventHandler.onChildChanged(dataSnapshotMock, "previousChildKey");
    eventHandler.onChildMoved(dataSnapshotMock, "previousChildKey");
    eventHandler.onChildRemoved(dataSnapshotMock);

    verify(eventListenerMock, never()).handle(any());
  }

  @Test
  public void shouldSkipIfListenerIsNull() {
    EventHandler eventHandler = new EventHandler(null);
    eventHandler.onChildAdded(dataSnapshotMock, "previousChildKey");
  }
}
