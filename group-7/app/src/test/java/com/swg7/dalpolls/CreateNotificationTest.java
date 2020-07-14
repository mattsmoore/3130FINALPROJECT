package com.swg7.dalpolls;

import com.swg7.dalpolls.election.NewQuestionStatus;
import com.swg7.dalpolls.election.QuestionCreator;
import com.swg7.dalpolls.notification.NotificationCreator;

import org.junit.*;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CreateNotificationTest {

    private NotificationCreator nc;
    private static String VALID_CONTENT = "valid length content";
    private static String INVALID_CONTENT = "";

    @Before
    public void setup(){
        nc = new NotificationCreator(null);
    }

    @Test
    public void isContentLengthValidTest(){
        assertEquals(nc.isContentLengthValid(VALID_CONTENT), true);
    }

    @Test
    public void isContentNotEmptyTest(){
        assertEquals(nc.isContentLengthValid(INVALID_CONTENT), false);
    }
}
