describe("Dashboard E2E", () => {
  beforeEach(() => {
    cy.intercept("GET", "/api/notifications/log", {
      statusCode: 200,
      body: [
        {
          id: 1,
          type: "Notification",
          userName: "John Doe",
          category: "Sports",
          channel: "SMS",
          message: "Integration test",
          timestamp: new Date().toISOString(),
        },
      ],
    }).as("getLogs");

    cy.intercept("POST", "/api/notifications", {
      statusCode: 202,
      body: {},
    }).as("postNotification");
  });

  it("Given mocked history, When visiting dashboard, Then table shows the row", () => {
    cy.visit("/");

    cy.wait("@getLogs");

    cy.contains("Log History").should("be.visible");
    cy.contains("td", "John Doe").should("be.visible");
    cy.contains("td", "Sports").should("be.visible");
    cy.contains("td", "SMS").should("be.visible");
    cy.contains("td", "Integration test").should("be.visible");
  });

  it("Given empty form, When clicking Send, Then validation error appears", () => {
    cy.visit("/");

    // Disable HTML5 form validation so React's handleSubmit runs
    cy.get("form").invoke("attr", "novalidate", "novalidate");

    cy.contains("button", "Send").click();

    cy.contains("Message is required").should("be.visible");
  });

  it("Given valid form, When sending, Then API is called and success is shown", () => {
    cy.visit("/");

    // Change category to Finance
    cy.get('[aria-labelledby="category-label"]').click();
    cy.contains("li", "Finance").click();

    // Type message - use more specific selector
    cy.get("textarea[required]").type("Hello from E2E");

    // Send
    cy.contains("button", "Send").click();

    cy.wait("@postNotification").its("request.body").should("deep.include", {
      category: "Finance",
      message: "Hello from E2E",
    });

    cy.contains("Notification sent successfully").should("be.visible");
  });
});
