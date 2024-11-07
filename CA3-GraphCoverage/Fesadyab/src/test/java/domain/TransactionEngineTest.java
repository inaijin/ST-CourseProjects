package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionEngineTest {

    private TransactionEngine engine;

    @BeforeEach
    public void setUp() {
        engine = new TransactionEngine();
    }

    @Test
    @DisplayName("Should return 0 when no transactions exist for an account")
    public void testGetAverageTransactionAmountByAccount_NoTransactions() {
        assertEquals(0, engine.getAverageTransactionAmountByAccount(1));
    }

    @Test
    @DisplayName("Should return 0 when transactions exist but no account IDs match")
    public void testGetAverageTransactionAmountByAccount_NoMatchingAccountId() {
        Transaction txn1 = new Transaction();
        txn1.setTransactionId(1);
        txn1.setAccountId(202);
        txn1.setAmount(1000);
        txn1.setDebit(true);

        Transaction txn2 = new Transaction();
        txn2.setTransactionId(2);
        txn2.setAccountId(202);
        txn2.setAmount(2000);
        txn2.setDebit(true);

        engine.addTransactionAndDetectFraud(txn1);
        engine.addTransactionAndDetectFraud(txn2);

        assertEquals(0, engine.getAverageTransactionAmountByAccount(101));
    }

    @Test
    @DisplayName("Should calculate average transaction amount correctly")
    public void testGetAverageTransactionAmountByAccount_WithTransactions() {
        Transaction txn1 = new Transaction();
        txn1.setTransactionId(1);
        txn1.setAccountId(101);
        txn1.setAmount(1000);
        txn1.setDebit(true);

        Transaction txn2 = new Transaction();
        txn2.setTransactionId(2);
        txn2.setAccountId(101);
        txn2.setAmount(2000);
        txn2.setDebit(true);

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
        Transaction txn1 = new Transaction();
        txn1.setTransactionId(1);
        txn1.setAccountId(101);
        txn1.setAmount(1500);
        txn1.setDebit(true);

        engine.addTransactionAndDetectFraud(txn1);
        assertEquals(0, engine.getTransactionPatternAboveThreshold(1000));
    }

    @Test
    @DisplayName("Should continue loop when multiple transactions have the same ID")
    public void testGetTransactionPatternAboveThreshold_MultipleTransactionsSameId() {
        Transaction txn1 = new Transaction();
        txn1.setTransactionId(1);
        txn1.setAccountId(101);
        txn1.setAmount(1500);
        txn1.setDebit(true);

        Transaction txn2 = new Transaction();
        txn2.setTransactionId(1);
        txn2.setAccountId(101);
        txn2.setAmount(2500);
        txn2.setDebit(true);

        Transaction txn3 = new Transaction();
        txn3.setTransactionId(1);
        txn3.setAccountId(101);
        txn3.setAmount(3500);
        txn3.setDebit(true);

        engine.addTransactionAndDetectFraud(txn1);
        engine.addTransactionAndDetectFraud(txn2);
        engine.addTransactionAndDetectFraud(txn3);

        assertEquals(0, engine.getTransactionPatternAboveThreshold(1000));
    }

    @Test
    @DisplayName("Should return 0 when no transactions exceed the threshold")
    public void testGetTransactionPatternAboveThreshold_NoTransactionsAboveThreshold() {
        Transaction txn1 = new Transaction();
        txn1.setTransactionId(1);
        txn1.setAccountId(101);
        txn1.setAmount(500);
        txn1.setDebit(true);

        Transaction txn2 = new Transaction();
        txn2.setTransactionId(2);
        txn2.setAccountId(101);
        txn2.setAmount(700);
        txn2.setDebit(true);

        engine.addTransactionAndDetectFraud(txn1);
        engine.addTransactionAndDetectFraud(txn2);

        assertEquals(0, engine.getTransactionPatternAboveThreshold(1000));
    }

    @Test
    @DisplayName("Should return 0 when transaction pattern breaks above the threshold")
    public void testGetTransactionPatternAboveThreshold_BreaksPattern() {
        Transaction txn1 = new Transaction();
        txn1.setTransactionId(1);
        txn1.setAccountId(101);
        txn1.setAmount(1500);
        txn1.setDebit(true);

        Transaction txn2 = new Transaction();
        txn2.setTransactionId(2);
        txn2.setAccountId(101);
        txn2.setAmount(2500);
        txn2.setDebit(true);

        Transaction txn3 = new Transaction();
        txn3.setTransactionId(3);
        txn3.setAccountId(101);
        txn3.setAmount(4100);
        txn3.setDebit(true);

        engine.addTransactionAndDetectFraud(txn1);
        engine.addTransactionAndDetectFraud(txn2);
        engine.addTransactionAndDetectFraud(txn3);

        assertEquals(0, engine.getTransactionPatternAboveThreshold(1000));
    }

    @Test
    @DisplayName("Should detect consistent transaction pattern above threshold")
    public void testGetTransactionPatternAboveThreshold_ConsistentPattern() {
        Transaction txn1 = new Transaction();
        txn1.setTransactionId(1);
        txn1.setAccountId(101);
        txn1.setAmount(1500);
        txn1.setDebit(true);

        Transaction txn2 = new Transaction();
        txn2.setTransactionId(2);
        txn2.setAccountId(101);
        txn2.setAmount(2500);
        txn2.setDebit(true);

        Transaction txn3 = new Transaction();
        txn3.setTransactionId(3);
        txn3.setAccountId(101);
        txn3.setAmount(3500);
        txn3.setDebit(true);

        engine.addTransactionAndDetectFraud(txn1);
        engine.addTransactionAndDetectFraud(txn2);
        engine.addTransactionAndDetectFraud(txn3);

        assertEquals(1000, engine.getTransactionPatternAboveThreshold(1000));
    }

    @Test
    @DisplayName("Should return 0 when no previous transactions and amount does not exceed twice the average")
    public void testDetectFraudulentTransaction_NoPreviousTransactions_NoExcessiveDebit() {
        Transaction txn = new Transaction();
        txn.setTransactionId(1);
        txn.setAccountId(101);
        txn.setAmount(500);
        txn.setDebit(false);

        assertEquals(0, engine.detectFraudulentTransaction(txn));
    }

    @Test
    @DisplayName("Should detect fraud when no previous transactions and amount exceeds twice the average")
    public void testDetectFraudulentTransaction_NoPreviousTransactions_ExcessiveDebit() {
        Transaction txn = new Transaction();
        txn.setTransactionId(1);
        txn.setAccountId(101);
        txn.setAmount(1000);
        txn.setDebit(true);

        assertEquals(1000, engine.detectFraudulentTransaction(txn));
    }

    @Test
    @DisplayName("Should return 0 when transaction is already in history")
    public void testAddTransactionAndDetectFraud_DuplicateTransaction() {
        Transaction txn1 = new Transaction();
        txn1.setTransactionId(1);
        txn1.setAccountId(101);
        txn1.setAmount(500);
        txn1.setDebit(true);

        engine.addTransactionAndDetectFraud(txn1);

        assertEquals(0, engine.addTransactionAndDetectFraud(txn1));
    }

    @Test
    @DisplayName("Should add transaction without fraud detection")
    public void testAddTransactionAndDetectFraud_NoFraud() {
        Transaction txn1 = new Transaction();
        txn1.setTransactionId(1);
        txn1.setAccountId(101);
        txn1.setAmount(500);
        txn1.setDebit(false);

        int fraudScore = engine.addTransactionAndDetectFraud(txn1);
        assertEquals(0, fraudScore);
    }

    @Test
    @DisplayName("Should detect fraud when debit exceeds twice the average transaction amount")
    public void testAddTransactionAndDetectFraud_ExcessiveDebit() {
        Transaction txn1 = new Transaction();
        txn1.setTransactionId(1);
        txn1.setAccountId(101);
        txn1.setAmount(500);
        txn1.setDebit(false);

        Transaction txn2 = new Transaction();
        txn2.setTransactionId(2);
        txn2.setAccountId(101);
        txn2.setAmount(2000);
        txn2.setDebit(true);

        int fraudScore1 = engine.addTransactionAndDetectFraud(txn1);
        int fraudScore2 = engine.addTransactionAndDetectFraud(txn2);
        assertEquals(0, fraudScore1);
        assertEquals(1000, fraudScore2);
    }

    @Test
    @DisplayName("Should detect pattern above threshold when transactions match threshold criteria")
    public void testAddTransactionAndDetectFraud_PatternAboveThreshold() {
        Transaction txn1 = new Transaction();
        txn1.setTransactionId(1);
        txn1.setAccountId(101);
        txn1.setAmount(1500);
        txn1.setDebit(false);

        Transaction txn2 = new Transaction();
        txn2.setTransactionId(2);
        txn2.setAccountId(101);
        txn2.setAmount(2500);
        txn2.setDebit(false);

        Transaction txn3 = new Transaction();
        txn3.setTransactionId(3);
        txn3.setAccountId(101);
        txn3.setAmount(3500);
        txn3.setDebit(true);

        int fraudScore1 = engine.addTransactionAndDetectFraud(txn1);
        int fraudScore2 =engine.addTransactionAndDetectFraud(txn2);
        int fraudScore3 =engine.addTransactionAndDetectFraud(txn3);

        assertEquals(0, fraudScore1);
        assertEquals(0, fraudScore2);
        assertEquals(1000, fraudScore3);
    }

    @Test
    @DisplayName("Should return 0 for pattern above threshold when difference is inconsistent")
    public void testAddTransactionAndDetectFraud_InconsistentPattern() {
        Transaction txn1 = new Transaction();
        txn1.setTransactionId(1);
        txn1.setAccountId(103);
        txn1.setAmount(2000);
        txn1.setDebit(false);

        Transaction txn2 = new Transaction();
        txn2.setTransactionId(2);
        txn2.setAccountId(103);
        txn2.setAmount(3000);
        txn2.setDebit(false);

        Transaction txn3 = new Transaction();
        txn3.setTransactionId(3);
        txn3.setAccountId(103);
        txn3.setAmount(4500);
        txn3.setDebit(false);

        Transaction txn4 = new Transaction();
        txn4.setTransactionId(4);
        txn4.setAccountId(103);
        txn4.setAmount(50);
        txn4.setDebit(false);

        int fraudScore1 = engine.addTransactionAndDetectFraud(txn1);
        int fraudScore2 = engine.addTransactionAndDetectFraud(txn2);
        int fraudScore3 = engine.addTransactionAndDetectFraud(txn3);
        int fraudScore4 = engine.addTransactionAndDetectFraud(txn4);

        assertEquals(0, fraudScore1);
        assertEquals(0, fraudScore2);
        assertEquals(1000, fraudScore3);
        assertEquals(0, fraudScore4);
    }

    @Test
    @DisplayName("Should return 0 when transaction history has only one transaction")
    public void testGetTransactionPatternAboveThreshold_SingleTransactionInHistory() {
        Transaction txn1 = new Transaction();
        txn1.setTransactionId(1);
        txn1.setAccountId(101);
        txn1.setAmount(1500);
        txn1.setDebit(true);

        engine.addTransactionAndDetectFraud(txn1);
        assertEquals(0, engine.getTransactionPatternAboveThreshold(1000));
    }

    @Test
    @DisplayName("Should not detect fraud when debit amount does not exceed twice the average")
    public void testDetectFraudulentTransaction_DebitNotExceedingTwiceAverage() {
        Transaction txn1 = new Transaction();
        txn1.setTransactionId(1);
        txn1.setAccountId(101);
        txn1.setAmount(500);
        txn1.setDebit(true);
        engine.addTransactionAndDetectFraud(txn1);

        Transaction txn2 = new Transaction();
        txn2.setTransactionId(2);
        txn2.setAccountId(101);
        txn2.setAmount(1000);
        txn2.setDebit(true);

        int fraudScore = engine.addTransactionAndDetectFraud(txn2);
        assertEquals(0, fraudScore);
    }

    @Test
    @DisplayName("Should return false when comparing Transaction with non-Transaction object")
    public void testTransactionEquals_NonTransactionObject() {
        Transaction txn = new Transaction();
        txn.setTransactionId(1);
        txn.setAccountId(101);
        txn.setAmount(1000);
        txn.setDebit(true);

        String notATransaction = "Not a Transaction";
        assertNotEquals(txn, notATransaction);
    }
}
