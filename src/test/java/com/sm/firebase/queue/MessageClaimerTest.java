package com.sm.firebase.queue;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction.Handler;
import com.google.firebase.database.Transaction.Result;

public class MessageClaimerTest {
  @Mock
  private DataSnapshot dataSnapshotMock;
  @Mock
  private DatabaseReference queueRefMock;
  @Mock
  private DatabaseReference queueMessageRefMock;
  @Mock
  private DatabaseReference queueMessageInitRefMock;
  @Mock
  private Query queryMock;
  @Mock
  private DatabaseReference messageRefMock;
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

  MessageClaimer messageClaimer;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    when(queueRefMock.child("messages")).thenReturn(queueMessageRefMock);
    when(queueMessageRefMock.orderByChild("header/state")).thenReturn(queryMock);
    when(queryMock.equalTo("new")).thenReturn(queryMock);
    when(queryMock.limitToFirst(1)).thenReturn(queryMock);
    when(queueRefMock.push()).thenReturn(queueMessageInitRefMock);

    messageClaimer = new MessageClaimer(queueExecutorMock, messageListenerMock, queueRefMock);
  }

  @Test
  public void shouldInitMessageListener() throws Exception {
    messageClaimer.start();

    verify(queryMock).addChildEventListener(messageClaimer);
    verify(queueMessageRefMock).orderByChild("header/state");
    verify(queryMock).equalTo("new");
    verify(queryMock).limitToFirst(1);
  }

  @Test
  public void shouldCallHandlerInTransaction() throws Exception {
    when(dataSnapshotMock.getRef()).thenReturn(messageRefMock);
    when(paramMutableDataMock.getValue(Message.class)).thenReturn(new Message());

    messageClaimer.onChildAdded(dataSnapshotMock, "previousChildKey");

    verify(messageRefMock).runTransaction(eventHandlerCaptor.capture());
    assertNotNull(eventHandlerCaptor.getValue());
    Result result = eventHandlerCaptor.getValue().doTransaction(paramMutableDataMock);
    assertThat(result.isSuccess(), is(true));
    verify(paramMutableDataMock).setValue(messageCaptor.capture());
    assertThat(messageCaptor.getValue().getHeader().get("state"), is("in_progress"));
  }

  @Test
  public void shouldNotCallListenerForOtherMethodThenAddChild() {
    messageClaimer.onCancelled(databaseError);
    messageClaimer.onChildChanged(dataSnapshotMock, "previousChildKey");
    messageClaimer.onChildMoved(dataSnapshotMock, "previousChildKey");
    messageClaimer.onChildRemoved(dataSnapshotMock);

    verify(messageListenerMock, never()).handle(any());
  }
}
