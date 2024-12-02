package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionTest {
    int ACCOUNT_ID = 101;

    private Transaction txn1;

    private void setUpTransaction(Transaction transaction, int transID, int amount, Boolean debit) {
        transaction.setTransactionId(transID);
        transaction.setAccountId(ACCOUNT_ID);
        transaction.setAmount(amount);
        transaction.setDebit(debit);
    }

    @BeforeEach
    public void setUp() {
        txn1 = new Transaction();
        setUpTransaction(txn1, 1, 500, true);
    }

    @Test
    @DisplayName("Test getters and setters for Transaction fields")
    public void testTransactionGettersAndSetters() {
        assertEquals(1, txn1.getTransactionId());
        assertEquals(101, txn1.getAccountId());
        assertEquals(500, txn1.getAmount());
        assertTrue(txn1.isDebit());
    }

    @Test
    @DisplayName("Should return false when comparing Transaction with non-Transaction object")
    public void testTransactionEquals_NonTransactionObject() {
        String notATransaction = "Not a Transaction";
        assertNotEquals(txn1, notATransaction);
    }

}
