# GUI Configuration

Customize every aspect of the graphical interfaces.

## Overview

The `gui.yml` file dictates the sizes, titles, layout matrices, and filler items of all in-game menus.

## Elements Explained

* **Size**: Multiples of 9 (e.g., 27, 36, 45).
* **Title**: Menu display name.
* **Layout**: A character matrix representing inventory slots.
* **Items**: Definitions for filler and action items mapped to layout characters.
* **Pagination**: Buttons mapped to `PREVIOUS` and `NEXT`.
* **Close**: A button mapped to close the inventory.

## Example

```yaml
booster_menu:
  title: "&8Your Boosters"
  size: 54
  layout:
    - "#########"
    - "#.......#"
    - "#.......#"
    - "#.......#"
    - "#########"
  items:
    "#":
      material: GRAY_STAINED_GLASS_PANE
      name: " "
```

## Available Menus

* **Booster Menu (`/nb open`)**: Main token repository.
* **Confirm Menu**: Confirmation interface before activation.
* **Active Menu (`/nb active`)**: View currently running multipliers.
* **Admin Menu (`/nb admin`)**: Backend administrative tools.

---

[← Previous](Permissions) | [Home](Home) | [Next →](Messages)
