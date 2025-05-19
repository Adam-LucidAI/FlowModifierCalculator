package org.example.flowmod;

import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.example.flowmod.HoleSpec;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.api.FxToolkit;

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
        clickOn("#headerLengthMm").write("100");
        clickOn("#drillMinMm").write("1.0");
        clickOn("#calculateButton");

        FxToolkit.setupFixture(() -> {
            TableView<HoleSpec> table = lookup("#resultTable").query();
            assertTrue(table.getItems().size() > 0);
        });
    }
}

