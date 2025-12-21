import NotificationForm from "./NotificationForm";

describe("NotificationForm component", () => {
  it("Given category and message, When sending, Then API and onSent are called", () => {
    const onSent = cy.spy().as("onSent");

    cy.intercept("POST", "/api/notifications", {
      statusCode: 202,
      body: {},
    }).as("notify");

    cy.mount(<NotificationForm onSent={onSent} />);

    // Select category "Finance"
    cy.get('[aria-labelledby="category-label"]').click();
    cy.contains("li", "Finance").click();

    // Type message
    cy.get("textarea[required]").type("Hello from Cypress");

    // Click Send
    cy.contains("button", "Send").click();

    // Assert API was called with expected payload
    cy.wait("@notify").its("request.body").should("deep.include", {
      category: "Finance",
      message: "Hello from Cypress",
    });

    // Assert onSent prop was called
    cy.get("@onSent").should("have.been.called");
  });

  it("Given empty message, When sending, Then validation error is shown", () => {
    cy.mount(<NotificationForm />);

    // Disable HTML5 form validation so React's handleSubmit runs
    cy.get("form").invoke("attr", "novalidate", "novalidate");

    cy.contains("button", "Send").click();

    // Assert Snackbar/Alert shows the error text
    cy.contains("Message is required").should("be.visible");
  });
});
