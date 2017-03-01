package com.sm.firebasequeue;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction.Handler;
import com.google.firebase.database.Transaction.Result;

public class MessageClaimerTest {
  @Mock
  private DataSnapshot dataSnapshotMock;
  @Mock
  private DatabaseReference databaseReferenceMock;
  @Mock
  private DatabaseError databaseError;
  @Mock
  private QueueExecutor queueExecutorMock;
  @Mock
  private MessageListener messageListenerMock;
  @Mock
  private MutableData paramMutableDataMock;

  @Captor
  private ArgumentCaptor<Handler> eventHandlerCaptor;

  @Captor
  private ArgumentCaptor<Message> messageCaptor;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void shouldCallHandlerInTransaction() throws Exception {
    when(dataSnapshotMock.getRef()).thenReturn(databaseReferenceMock);

    MessageClaimer eventHandler = new MessageClaimer(queueExecutorMock,
        messageListenerMock);
    eventHandler.onChildAdded(dataSnapshotMock, "previousChildKey");

    verify(databaseReferenceMock).runTransaction(eventHandlerCaptor.capture());

    assertNotNull(eventHandlerCaptor.getValue());

    when(paramMutableDataMock.getValue(Message.class))
        .thenReturn(new Message());

    Result result = eventHandlerCaptor.getValue()
        .doTransaction(paramMutableDataMock);
    assertThat(result.isSuccess(), is(true));

    verify(paramMutableDataMock).setValue(messageCaptor.capture());

    assertThat(messageCaptor.getValue().getHeader().get("state"),
        is("in_progress"));
  }

  @Test
  public void shouldNotCallListenerForOtherMethodThenAddChild() {
    MessageClaimer eventHandler = new MessageClaimer(queueExecutorMock,
        messageListenerMock);
    eventHandler.onCancelled(databaseError);
    eventHandler.onChildChanged(dataSnapshotMock, "previousChildKey");
    eventHandler.onChildMoved(dataSnapshotMock, "previousChildKey");
    eventHandler.onChildRemoved(dataSnapshotMock);

    verify(messageListenerMock, never()).handle(any());
  }
}
