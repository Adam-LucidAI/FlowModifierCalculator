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
2. Enter the pipe I.D., flow rate (in GPM) and minimum drill size (defaults to 4 mm).
   The tool converts GPM to litres per minute using 1 GPM = 3.785 L/min.
3. Click **Calculate** to auto-design the perforated strip.
   Hole diameters are automatically capped at 25 % of the pipe I.D.
4. **Export CSV** saves the hole layout; other actions are marked TODO.
5. Available drill diameters are listed in `src/main/resources/drills.json`.
   Edit this file to match your own drill set.

The interface is shown below:

![GUI screenshot](screenshots/gui.png)
