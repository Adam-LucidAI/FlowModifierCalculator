package org.example.flowmod;

import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

public class FlowModifierUITest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        new FlowModifierUI().start(stage);
    }

    @Test
    void tableHasExpectedRows() {
        clickOn("#innerDiameterMm").write("20");
        clickOn("#flowRateLpm").write("5");
        clickOn("#modifierLengthMm").write("100");
        clickOn("#openAreaPercent").write("30");
        clickOn("#faceVelocityMaxMs").write("1.0");
        clickOn("#drillMinMm").write("1.0");
        clickOn("#drillMaxMm").write("5.0");
        clickOn("#calculateButton");

        TableView<HoleSpec> table = lookup("#resultTable").query();
        assertEquals(10, table.getItems().size());
    }
}

