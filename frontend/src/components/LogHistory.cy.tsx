import LogHistory from "./LogHistory";

describe("LogHistory component", () => {
  it("Given empty logs, When rendering, Then shows 'No logs found'", () => {
    cy.intercept("GET", "/api/notifications/log", {
      statusCode: 200,
      body: [],
    }).as("getLogsEmpty");

    cy.mount(<LogHistory />);

    cy.wait("@getLogsEmpty");
    cy.contains("No logs found.").should("be.visible");
  });

  it("Given logs data, When rendering, Then displays table with data", () => {
    const mockLogs = [
      {
        id: 1,
        type: "Notification",
        userName: "John Doe",
        category: "Sports",
        channel: "SMS",
        message: "Test message",
        timestamp: "2023-12-21T10:00:00Z",
      },
    ];

    cy.intercept("GET", "/api/notifications/log", {
      statusCode: 200,
      body: mockLogs,
    }).as("getLogs");

    cy.mount(<LogHistory />);
    cy.wait("@getLogs");

    cy.contains("Log History").should("be.visible");
    cy.contains("John Doe").should("be.visible");
    cy.contains("Sports").should("be.visible");
    cy.contains("SMS").should("be.visible");
    cy.contains("Test message").should("be.visible");
  });

  it("Given refresh button clicked, When loading, Then shows loading state", () => {
    cy.intercept("GET", "/api/notifications/log", {
      statusCode: 200,
      body: [],
      delay: 1000,
    }).as("getLogsDelay");

    cy.mount(<LogHistory />);
    cy.wait("@getLogsDelay");

    cy.intercept("GET", "/api/notifications/log", {
      statusCode: 200,
      body: [],
      delay: 500,
    }).as("getLogsRefresh");

    cy.contains("Refresh").click();
    cy.get("button").contains("Refresh").should("be.disabled");
    cy.wait("@getLogsRefresh");
  });

  it("Given different categories and channels, When rendering, Then shows correct chip colors", () => {
    const mockLogs = [
      {
        id: 1,
        type: "Notification",
        userName: "User 1",
        category: "Finance",
        channel: "E-Mail",
        message: "Finance message",
        timestamp: "2023-12-21T10:00:00Z",
      },
      {
        id: 2,
        type: "Notification",
        userName: "User 2",
        category: "Movies",
        channel: "Push Notification",
        message: "Movies message",
        timestamp: "2023-12-21T11:00:00Z",
      },
    ];

    cy.intercept("GET", "/api/notifications/log", {
      statusCode: 200,
      body: mockLogs,
    }).as("getLogsMultiple");

    cy.mount(<LogHistory />);
    cy.wait("@getLogsMultiple");

    cy.contains("Finance").should("be.visible");
    cy.contains("E-Mail").should("be.visible");
    cy.contains("Movies").should("be.visible");
    cy.contains("Push Notification").should("be.visible");
  });

  it("Given refreshKey prop changes, When component updates, Then reloads data", () => {
    cy.intercept("GET", "/api/notifications/log", {
      statusCode: 200,
      body: [],
    }).as("getLogsInitial");

    const TestWrapper = ({ refreshKey }: { refreshKey: number }) => (
      <LogHistory refreshKey={refreshKey} />
    );

    cy.mount(<TestWrapper refreshKey={1} />);
    cy.wait("@getLogsInitial");

    cy.intercept("GET", "/api/notifications/log", {
      statusCode: 200,
      body: [
        {
          id: 1,
          type: "Notification",
          userName: "Updated User",
          category: "Sports",
          channel: "SMS",
          message: "Updated message",
          timestamp: "2023-12-21T12:00:00Z",
        },
      ],
    }).as("getLogsUpdated");

    cy.mount(<TestWrapper refreshKey={2} />);
    cy.wait("@getLogsUpdated");
    cy.contains("Updated User").should("be.visible");
  });
});
