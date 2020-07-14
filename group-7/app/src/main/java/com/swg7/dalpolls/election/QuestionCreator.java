package com.swg7.dalpolls.election;

import java.util.List;

public class QuestionCreator {



    public NewQuestionStatus validateNewQuestion(String question, List<String> answers) {
        if (!isValidQuestion(question)) {
            return NewQuestionStatus.EMPTY_QUESTION;
        }

        if(!isValidAnswers(answers)){
            return NewQuestionStatus.EMPTY_ANSWER;
        }

        return NewQuestionStatus.VALID; //TODO: implement
    }

    private boolean isValidQuestion(String question){
        return question != null && !question.isEmpty();
    }

    private boolean isValidAnswers(List<String> answers){
        if(answers.isEmpty()){
            return false;
        }
        for(String answer: answers){
            if(!isValidAnswer(answer)){
                return false;
            }
        }
        return true;
    }

    private boolean isValidAnswer(String answer){
        return answer != null && !answer.isEmpty();
    }

}
