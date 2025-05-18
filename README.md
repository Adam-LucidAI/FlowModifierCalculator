# FlowModifierCalculator

This tool designs graduated-hole flow modifiers and now includes a simplified JavaFX interface.

## Build & Run

```
mvn test
mvn javafx:run
```

The project still references the [JitPack](https://jitpack.io) repository, but
STL export is temporarily disabled while the library selection is finalised.

The CLI version can still be run using the exec plugin profile.

### How to use

1. Run `mvn javafx:run`.
2. Enter the pipe I.D., modifier length, flow rate and drill size range.
3. Click **Calculate** to populate the table and summary.
4. Use **Validate flow** to check the design.

The interface is shown below:

![GUI screenshot](screenshots/gui.png)
