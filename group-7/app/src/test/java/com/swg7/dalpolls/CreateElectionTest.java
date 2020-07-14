package com.swg7.dalpolls;

import com.swg7.dalpolls.election.Election;

import org.junit.Before;
import org.junit.Test;

public class CreateElectionTest {

    private ElectionCreator eValidator;

    @Before
    public void setUp() {
        eValidator = new ElectionCreator();
    }

    @Test
    public void date_isValid() {
        boolean valid = new ElectionCreator().setStartDate("05/11/2019").setEndDate("04/12/2019").hasValidDate();
        assert !valid;
    }

    @Test
    public void date_FormatValid() {
        boolean valid = new ElectionCreator().setStartDate("01/01/2020").setEndDate("01/19/2020").hasValidDate();
        assert valid;
    }

    @Test
    public void date_FormatInvalid() {
        boolean valid = new ElectionCreator().setStartDate("01/01/2020").setEndDate("2029/19/20").hasValidDate();
        assert !valid;
    }

    @Test
    public void date_DayMatchesMonthTrue() {
        boolean valid = new ElectionCreator().setStartDate("02/20/2011").setEndDate("02/28/2011").hasValidDate();
        assert valid;
    }

    @Test
    public void election_hasTitle() {
        boolean valid = eValidator.setName("new Election").hasValidTitle();
        assert valid;
    }

    @Test
    public void election_hasNoTitle() {
        boolean valid = eValidator.setName("").hasValidTitle();
        assert !valid;
    }

    @Test
    public void test_newElection_beginsAllFalseFlags() {
        Election e = new Election();
        assert !e.isFinal();
        assert !e.isOpen();
        assert !e.isPublic();
        assert !e.areResultsVisible();
        assert e.isEmpty();
    }

    @Test
    public void test_emptyElection_cannotBeFinalized() {
        Election e = new Election();
        assert !e.canFinalize();
    }

    @Test
    public void test_emptyElection_cannotBePreviewed() {
        Election e = new Election();
        assert !e.canPreview();
    }

    @Test
    public void test_emptyElection_cannotBePublished() {
        Election e = new Election();
        assert !e.canPublish();
    }

    @Test
    public void test_emptyElection_cannotBeOpen() {
        Election e = new Election();
        assert !e.canOpen();
    }

    @Test
    public void test_emptyElection_cannotResultVisible() {
        Election e = new Election();
        assert !e.canMakeResultVisible();
    }

    @Test
    public void test_VisableSet() {
        Election e = new Election();
        e.publish();
        assert e.isPublic();
    }

    @Test
    public void test_VotableSet() {
        Election e = new Election();
        e.open();
        assert e.isOpen();
    }

    @Test
    public void test_ResultSet() {
        Election e = new Election();
        e.showResults();
        assert e.areResultsVisible();
    }

    //Test Cloning
    @Test
    public void test_cloneElection() {
        Election e = new Election();
        Election clone = e.clone();
        assert (e.name == clone.name &&
                e.id == clone.id &&
                e.listQ.equals(clone));
    }

}
