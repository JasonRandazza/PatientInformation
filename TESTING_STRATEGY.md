# Testing Strategy for Stripe Integration in PaymentController

This document outlines the conceptual unit and integration test cases for the Stripe payment integration within `PaymentController.java`. These tests aim to ensure the reliability and correctness of the payment processing logic.

**Target Test File:** `src/test/java/org/example/patientinformation/PaymentControllerTest.java`

**General Considerations for Testing:**

*   **Mocking Dependencies:** Thorough testing will require mocking external dependencies and UI components.
    *   **Firestore:** Use a mock Firestore instance. A utility method like `FirestoreContext.setTestFirestore()` could be employed if available, or a mocking framework like Mockito can be used to mock `Firestore`, `CollectionReference`, `DocumentReference`, `WriteBatch`, etc.
    *   **Stripe API:** Mock static methods of the `Stripe` class (e.g., `PaymentIntent.create()`) to simulate various API responses (success, failure, specific statuses) without making actual network calls. Tools like Mockito's `mockStatic` can be used. The `Stripe.apiKey` should be set to a test key during setup.
    *   **`LoggedInUser`:** Mock static methods of `LoggedInUser` (e.g., `getEmail()`) to provide a consistent test user.
    *   **`BillingManager`:** Mock `BillingManager` to control the billing records data used in tests.
    *   **JavaFX UI Components:**
        *   **Alerts:** Capture or mock calls to `Alert.showAndWait()` to verify that the correct types of alerts (Information, Warning, Error) and messages are displayed.
        *   **Stage:** Mock the `Stage` and verify calls to `close()` to ensure the payment window behaves as expected.
        *   **FXML Controls:** Mock FXML-injected controls like `RadioButton`, `TextField` to simulate user input and selections.
*   **Test Setup (`@BeforeEach`):** Each test method should have a clear setup phase where mocks are initialized, test data is prepared, and the `PaymentController` instance is created or configured.
*   **Test Teardown (`@AfterEach`):** Clean up any static mocks or state changes after each test to ensure test isolation.

## `PaymentController.java` Unit/Integration Tests

### 1. Test Successful Payment (Full Amount)

*   **Description:** Verifies the correct handling of a payment when the user chooses to pay the full billed amount and the Stripe payment is successful.
*   **Setup:**
    *   Mock `Firestore` and related classes.
    *   Mock `BillingManager.getBillingRecords()` to return a list of `BillingRecord` objects (e.g., two records totaling $50.00).
    *   Mock `LoggedInUser.getEmail()` to return a test email address.
    *   Mock `Stripe.apiKey` to be set.
    *   Mock `PaymentIntent.create()`:
        *   When called with parameters matching a total of 5000 cents, "usd", and "pm_card_visa", return a mock `PaymentIntent` object.
        *   The mock `PaymentIntent` should have `getId()` returning a test payment ID (e.g., "pi_test_success") and `getStatus()` returning "succeeded".
    *   In `PaymentController`:
        *   Set `fullAmountOption` to be selected.
        *   Set `otherAmountOption` to not be selected.
        *   Inject mock `billingController`.
*   **Action:**
    *   Call `paymentController.handleConfirmPayment()`.
*   **Assertions:**
    *   **Stripe API Call:** Verify `PaymentIntent.create()` was called exactly once with `PaymentIntentCreateParams` containing:
        *   Amount: `5000L`
        *   Currency: `"usd"`
        *   PaymentMethod: `"pm_card_visa"`
        *   Confirm: `true`
    *   **Firestore Interaction:**
        *   Verify that for each original billing record, data was added to the `users/{testEmail}/payment_history` collection.
        *   Verify that each document in `payment_history` contains the fields `stripePaymentIntentId` (with value "pi_test_success") and `stripePaymentStatus` (with value "succeeded").
        *   Verify that all documents were deleted from the `users/{testEmail}/billing` collection.
        *   Verify `WriteBatch.commit()` was called.
    *   **BillingManager:** Verify `BillingManager.getBillingRecords().clear()` was called.
    *   **UI Feedback:**
        *   Verify that an `Alert` of type `INFORMATION` was shown, containing the success message and the payment ID "pi_test_success".
        *   Verify `Stage.close()` was called on the payment window's stage.
    *   **BillingController Refresh:** Verify `billingController.refreshBilling()` was called.

### 2. Test Successful Payment (Custom Amount)

*   **Description:** Verifies correct handling when the user inputs a valid custom amount and the Stripe payment is successful.
*   **Setup:**
    *   Similar to "Test Successful Payment (Full Amount)".
    *   Mock `BillingManager.getBillingRecords()` (e.g., to return records totaling $100.00, to ensure it's different from custom).
    *   In `PaymentController`:
        *   Set `otherAmountOption` to be selected.
        *   Set `fullAmountOption` to not be selected.
        *   Set `customAmountField.getText()` to return "25.50".
    *   Mock `PaymentIntent.create()`:
        *   When called with parameters matching 2550 cents, "usd", and "pm_card_visa", return a mock `PaymentIntent` object with status "succeeded" and ID "pi_custom_success".
*   **Action:**
    *   Call `paymentController.handleConfirmPayment()`.
*   **Assertions:**
    *   **Stripe API Call:** Verify `PaymentIntent.create()` was called with `PaymentIntentCreateParams` containing:
        *   Amount: `2550L`
        *   Currency: `"usd"`
    *   **Firestore Interaction:**
        *   Verify data transfer from `billing` to `payment_history` with `stripePaymentIntentId` ("pi_custom_success") and `stripePaymentStatus` ("succeeded").
        *   Verify `billing` collection is cleared.
    *   **BillingManager:** Verify `BillingManager.getBillingRecords().clear()` was called.
    *   **UI Feedback:**
        *   Verify `Alert` of type `INFORMATION` with the correct message and payment ID "pi_custom_success".
        *   Verify `Stage.close()` was called.
    *   **BillingController Refresh:** Verify `billingController.refreshBilling()` was called.

### 3. Test Payment Failure (Stripe API Error)

*   **Description:** Verifies error handling when the Stripe API call itself fails (e.g., network issue, invalid API key).
*   **Setup:**
    *   Mock `Firestore`, `BillingManager`, `LoggedInUser` as in successful tests.
    *   Select either "full amount" or set a "custom amount".
    *   Mock `Stripe.apiKey` to be set.
    *   Mock `PaymentIntent.create()` to throw a `StripeException` (e.g., `new InvalidRequestException("Invalid API Key", null, null, null, 401, null)`).
*   **Action:**
    *   Call `paymentController.handleConfirmPayment()`.
*   **Assertions:**
    *   **UI Feedback:**
        *   Verify that an `Alert` of type `ERROR` was shown.
        *   Verify the alert message contains the error message from the `StripeException` (e.g., "Invalid API Key").
        *   Verify `Stage.close()` was NOT called.
    *   **Firestore Interaction:**
        *   Verify no documents were added to `payment_history`.
        *   Verify no documents were deleted from `billing`.
        *   Verify `WriteBatch.commit()` was NOT called.
    *   **BillingManager:** Verify `BillingManager.getBillingRecords().clear()` was NOT called.
    *   **BillingController Refresh:** Verify `billingController.refreshBilling()` was NOT called.

### 4. Test Payment Failure (PaymentIntent Non-Succeeded Status)

*   **Description:** Verifies error handling when the Stripe API call is successful, but the PaymentIntent returns a non-"succeeded" status (e.g., "requires_payment_method", "failed").
*   **Setup:**
    *   Mock `Firestore`, `BillingManager`, `LoggedInUser`.
    *   Select either "full amount" or set a "custom amount".
    *   Mock `Stripe.apiKey` to be set.
    *   Mock `PaymentIntent.create()` to return a mock `PaymentIntent` object with `getStatus()` returning "requires_payment_method" (or "failed", "requires_action", etc.).
*   **Action:**
    *   Call `paymentController.handleConfirmPayment()`.
*   **Assertions:**
    *   **UI Feedback:**
        *   Verify an `Alert` of type `ERROR` was shown.
        *   Verify the alert message contains the non-succeeded status (e.g., "Payment failed or requires further action. Status: requires_payment_method").
        *   Verify `Stage.close()` was NOT called.
    *   **Firestore Interaction:**
        *   Verify no data modifications in Firestore (`billing` and `payment_history` remain unchanged).
    *   **BillingManager:** Verify `BillingManager.getBillingRecords().clear()` was NOT called.
    *   **BillingController Refresh:** Verify `billingController.refreshBilling()` was NOT called.

### 5. Test Input Validation (Custom Amount)

*   **Description:** Verifies that user input for custom payment amounts is validated correctly before attempting any Stripe API calls or Firestore operations.
*   **Test Cases:**
    *   **Empty Amount:**
        *   **Setup:** `otherAmountOption` selected, `customAmountField.getText()` returns `""` or `null`.
        *   **Action:** Call `handleConfirmPayment()`.
        *   **Assertions:** Warning alert "Please enter a payment amount.", no Stripe call, no Firestore changes.
    *   **Non-Numeric Amount:**
        *   **Setup:** `otherAmountOption` selected, `customAmountField.getText()` returns `"abc"`.
        *   **Action:** Call `handleConfirmPayment()`.
        *   **Assertions:** Warning alert "Invalid amount. Please enter a valid number.", no Stripe call, no Firestore changes.
    *   **Zero Amount:**
        *   **Setup:** `otherAmountOption` selected, `customAmountField.getText()` returns `"0.00"` or `"0"`.
        *   **Action:** Call `handleConfirmPayment()`.
        *   **Assertions:** Warning alert "Amount must be greater than 0.", no Stripe call, no Firestore changes.
    *   **Negative Amount:**
        *   **Setup:** `otherAmountOption` selected, `customAmountField.getText()` returns `"-10.00"`.
        *   **Action:** Call `handleConfirmPayment()`.
        *   **Assertions:** Warning alert "Amount must be greater than 0.", no Stripe call, no Firestore changes.
    *   **Full Amount is Zero/Negative (Edge case for full amount option):**
        *   **Setup:** `fullAmountOption` selected. Mock `BillingManager.getBillingRecords()` to result in a sum of 0 or less.
        *   **Action:** Call `handleConfirmPayment()`.
        *   **Assertions:** Warning alert "There is no amount to pay or the bill is zero.", no Stripe call, no Firestore changes.
*   **General Assertions for all Input Validation Cases:**
    *   Verify `PaymentIntent.create()` was NOT called.
    *   Verify no interactions with Firestore (no reads from `billing`, no writes to `payment_history`).
    *   Verify `Stage.close()` was NOT called.
    *   Verify `BillingManager.getBillingRecords().clear()` was NOT called.
    *   Verify `billingController.refreshBilling()` was NOT called.
