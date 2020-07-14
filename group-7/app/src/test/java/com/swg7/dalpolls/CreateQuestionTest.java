package com.swg7.dalpolls;

import com.swg7.dalpolls.election.NewQuestionStatus;
import com.swg7.dalpolls.election.QuestionCreator;

import org.junit.*;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CreateQuestionTest {

    private QuestionCreator questionCreator;

    private static final String EMPTY_STRING = "";
    private static final String EMPTY_QUESTION = "";
    private static final String VALID_QUESTION = "Does this test pass?";
    private static final String VALID_ANSWER_1 = "Yes";
    private static final String VALID_ANSWER_2 = "No";
    private static final String VALID_ANSWER_3 = "Maybe?";

    private List<String> answers;

    @Before
    public void setup(){
        answers = new LinkedList<>();
        questionCreator = new QuestionCreator();
    }

    @Test
    public void validQuestionOneAnswer(){
        answers.add(VALID_ANSWER_1);
        assertEquals(questionCreator.validateNewQuestion(VALID_QUESTION, answers), NewQuestionStatus.VALID);
    }

    @Test
    public void validQuestionMultipleAnswers(){
        answers.add(VALID_ANSWER_1);
        answers.add(VALID_ANSWER_2);
        answers.add(VALID_ANSWER_3);
        assertEquals(questionCreator.validateNewQuestion(VALID_QUESTION, answers),NewQuestionStatus.VALID);
    }

    @Test
    public void emptyQuestion(){
        assertEquals(questionCreator.validateNewQuestion(EMPTY_QUESTION, answers), NewQuestionStatus.EMPTY_QUESTION);
    }

    @Test
    public void noAnswers(){
        assertEquals(questionCreator.validateNewQuestion(VALID_QUESTION, answers), NewQuestionStatus.EMPTY_ANSWER);
    }

    @Test
    public void emptyAnswer(){
        answers.add(EMPTY_STRING);
        assertEquals(questionCreator.validateNewQuestion(VALID_QUESTION, answers), NewQuestionStatus.EMPTY_ANSWER);
    }

}
