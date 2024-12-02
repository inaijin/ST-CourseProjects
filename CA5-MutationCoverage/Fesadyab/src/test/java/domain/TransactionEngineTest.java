package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionEngineTest {
    int ACCOUNT_ID = 101;

    private TransactionEngine engine;

    private Transaction txn1;
    private Transaction txn2;
    private Transaction txn3;

    private void setUpTransaction(Transaction transaction, int transID, int amount, Boolean debit) {
        transaction.setTransactionId(transID);
        transaction.setAccountId(ACCOUNT_ID);
        transaction.setAmount(amount);
        transaction.setDebit(debit);
    }

    @BeforeEach
    public void setUp() {
        engine = new TransactionEngine();

        txn1 = new Transaction();
        setUpTransaction(txn1, 1, 500, true);

        txn2 = new Transaction();
        setUpTransaction(txn2, 2, 2000, true);

        txn3 = new Transaction();
        setUpTransaction(txn3, 3, 1500, true);
    }

    @Test
    @DisplayName("Should return 0 when no transactions exist for an account")
    public void testGetAverageTransactionAmountByAccount_NoTransactions() {
        assertEquals(0, engine.getAverageTransactionAmountByAccount(1));
    }

    @Test
    @DisplayName("Should return 0 when transactions exist but no account IDs match")
    public void testGetAverageTransactionAmountByAccount_NoMatchingAccountId() {
        txn1.setAccountId(202);
        txn2.setAccountId(202);

        engine.addTransactionAndDetectFraud(txn1);
        engine.addTransactionAndDetectFraud(txn2);

        assertEquals(0, engine.getAverageTransactionAmountByAccount(101));
    }

    @Test
    @DisplayName("Should calculate average transaction amount correctly")
    public void testGetAverageTransactionAmountByAccount_WithTransactions() {
        txn1.setAmount(1000);
        txn2.setAmount(2000);

        engine.addTransactionAndDetectFraud(txn1);
        engine.addTransactionAndDetectFraud(txn2);

        assertEquals(1500, engine.getAverageTransactionAmountByAccount(101));
    }

    @Test
    @DisplayName("Should return 0 for transaction pattern above threshold when history is empty")
    public void testGetTransactionPatternAboveThreshold_EmptyHistory() {
        assertEquals(0, engine.getTransactionPatternAboveThreshold(1000));
    }

    @Test
    @DisplayName("Should return 0 for transaction pattern when there is only one transaction")
    public void testGetTransactionPatternAboveThreshold_SingleTransaction() {
        engine.addTransactionAndDetectFraud(txn3);

        assertEquals(0, engine.getTransactionPatternAboveThreshold(1000));
    }

    @Test
    @DisplayName("Should continue loop when multiple transactions have the same ID")
    public void testGetTransactionPatternAboveThreshold_MultipleTransactionsSameId() {
        txn2.setTransactionId(1);
        txn3.setTransactionId(1);

        engine.addTransactionAndDetectFraud(txn1);
        engine.addTransactionAndDetectFraud(txn2);
        engine.addTransactionAndDetectFraud(txn3);

        assertEquals(0, engine.getTransactionPatternAboveThreshold(1000));
    }

    @Test
    @DisplayName("Should return 0 when no transactions exceed the threshold")
    public void testGetTransactionPatternAboveThreshold_NoTransactionsAboveThreshold() {
        txn1.setAmount(500);
        txn2.setAmount(700);

        engine.addTransactionAndDetectFraud(txn1);
        engine.addTransactionAndDetectFraud(txn2);

        assertEquals(0, engine.getTransactionPatternAboveThreshold(1000));
    }

    @Test
    @DisplayName("Should return 0 when transaction pattern breaks above the threshold")
    public void testGetTransactionPatternAboveThreshold_BreaksPattern() {
        txn2.setAmount(2500);
        txn3.setAmount(4100);

        engine.addTransactionAndDetectFraud(txn1);
        engine.addTransactionAndDetectFraud(txn2);
        engine.addTransactionAndDetectFraud(txn3);

        assertEquals(0, engine.getTransactionPatternAboveThreshold(1000));
    }

    @Test
    @DisplayName("Should detect consistent transaction pattern above threshold")
    public void testGetTransactionPatternAboveThreshold_ConsistentPattern() {
        txn1.setAmount(1500);
        txn2.setAmount(2500);
        txn3.setAmount(3500);

        engine.addTransactionAndDetectFraud(txn1);
        engine.addTransactionAndDetectFraud(txn2);
        engine.addTransactionAndDetectFraud(txn3);

        assertEquals(1000, engine.getTransactionPatternAboveThreshold(1000));
    }

    @Test
    @DisplayName("Should handle exact and boundary values for threshold")
    public void testThresholdBoundaryValues() {
        txn1.setAmount(1000);
        txn2.setAmount(999);
        txn3.setAmount(1001);

        engine.addTransactionAndDetectFraud(txn1);
        engine.addTransactionAndDetectFraud(txn2);
        engine.addTransactionAndDetectFraud(txn3);

        assertEquals(1, engine.getTransactionPatternAboveThreshold(1000));
    }

    @Test
    @DisplayName("Should return 0 when no previous transactions and amount does not exceed twice the average")
    public void testDetectFraudulentTransaction_NoPreviousTransactions_NoExcessiveDebit() {
        txn1.setDebit(false);

        assertEquals(0, engine.detectFraudulentTransaction(txn1));
    }

    @Test
    @DisplayName("Should detect fraud when no previous transactions and amount exceeds twice the average")
    public void testDetectFraudulentTransaction_NoPreviousTransactions_ExcessiveDebit() {
        txn1.setAmount(1000);

        assertEquals(1000, engine.detectFraudulentTransaction(txn1));
    }

    @Test
    @DisplayName("Should return 0 when transaction is already in history")
    public void testAddTransactionAndDetectFraud_DuplicateTransaction() {
        engine.addTransactionAndDetectFraud(txn1);
        int result = engine.addTransactionAndDetectFraud(txn1);

        assertEquals(0, result);

        assertEquals(1, engine.transactionHistory.stream().filter(txn -> txn.equals(txn1)).count());
    }

    @Test
    @DisplayName("Should add transaction without fraud detection")
    public void testAddTransactionAndDetectFraud_NoFraud() {
        txn1.setDebit(false);
        int fraudScore = engine.addTransactionAndDetectFraud(txn1);

        assertEquals(0, fraudScore);

        assertTrue(engine.transactionHistory.contains(txn1));
    }

    @Test
    @DisplayName("Should detect fraud when debit exceeds twice the average transaction amount")
    public void testAddTransactionAndDetectFraud_ExcessiveDebit() {
        engine.addTransactionAndDetectFraud(txn1);
        int fraudScore = engine.addTransactionAndDetectFraud(txn2);

        assertEquals(1000, fraudScore);

        assertTrue(engine.transactionHistory.contains(txn1));
        assertTrue(engine.transactionHistory.contains(txn2));
    }

    @Test
    @DisplayName("Should detect pattern above threshold when transactions match threshold criteria")
    public void testAddTransactionAndDetectFraud_PatternAboveThreshold() {
        txn1.setAmount(1500);
        txn2.setAmount(2500);
        txn3.setAmount(3500);
        txn3.setDebit(true);

        engine.addTransactionAndDetectFraud(txn1);
        engine.addTransactionAndDetectFraud(txn2);
        int fraudScore = engine.addTransactionAndDetectFraud(txn3);

        assertEquals(1000, fraudScore);

        assertTrue(engine.transactionHistory.contains(txn1));
        assertTrue(engine.transactionHistory.contains(txn2));
        assertTrue(engine.transactionHistory.contains(txn3));
    }

    @Test
    @DisplayName("Should return 0 for pattern above threshold when difference is inconsistent")
    public void testAddTransactionAndDetectFraud_InconsistentPattern() {
        txn1.setAmount(2000);
        txn2.setAmount(3000);
        txn3.setAmount(4500);

        Transaction txn4 = new Transaction();
        setUpTransaction(txn4, 4, 50, false);

        engine.addTransactionAndDetectFraud(txn1);
        engine.addTransactionAndDetectFraud(txn2);
        int fraudScore1 = engine.addTransactionAndDetectFraud(txn3);
        int fraudScore2 = engine.addTransactionAndDetectFraud(txn4);

        assertEquals(1000, fraudScore1);
        assertEquals(0, fraudScore2);

        assertTrue(engine.transactionHistory.contains(txn1));
        assertTrue(engine.transactionHistory.contains(txn2));
        assertTrue(engine.transactionHistory.contains(txn3));
        assertTrue(engine.transactionHistory.contains(txn4));
    }

    @Test
    @DisplayName("Should not detect fraud when debit amount does not exceed twice the average")
    public void testDetectFraudulentTransaction_DebitNotExceedingTwiceAverage() {
        engine.addTransactionAndDetectFraud(txn1);
        txn2.setAmount(1000);

        int fraudScore = engine.addTransactionAndDetectFraud(txn2);

        assertEquals(0, fraudScore);

        assertTrue(engine.transactionHistory.contains(txn1));
        assertTrue(engine.transactionHistory.contains(txn2));
    }

    @Test
    @DisplayName("Should handle fraud detection at boundary")
    public void testFraudDetectionBoundary() {
        txn1.setAmount(500);
        txn2.setAmount(1000);
        txn3.setAmount(999);
        txn3.setDebit(true);

        engine.addTransactionAndDetectFraud(txn1);
        assertEquals(0, engine.addTransactionAndDetectFraud(txn2));
        assertEquals(0, engine.addTransactionAndDetectFraud(txn3));
    }

}
