# Random Item Picker

A Kotlin + Jetpack Compose app for managing lists of items and picking one at random, with tag-based filtering. Uses Room for storage and Material 3 for UI (dark mode + Android 12+ dynamic color built in).

## Opening the project
1. Unzip, then in Android Studio: **File → Open** and select the `RandomItemPicker` folder.
2. Let Gradle sync (it will pull the Compose BOM 2024.06.00, Room 2.6.1, Navigation Compose 2.7.7).
3. Run on a device/emulator with API 26+.

Requires JDK 17 and a recent Android Studio (Koala/Ladybug or newer) since it uses KSP instead of kapt for Room.

## Architecture
Simple MVVM, no Hilt — a hand-rolled `AppContainer` (see `di/AppContainer.kt`) builds the Room database and repository once in `RandomPickerApplication`, and a single `ViewModelFactory` hands them to whichever ViewModel a screen needs. Swap in Hilt/Koin later by replacing just those two files.

```
data/local/      Room entity, DAO, TypeConverter, database
data/repository/ ItemRepository — the only thing ViewModels talk to
di/              AppContainer + ViewModelFactory (manual DI)
ui/theme/        Material3 color scheme, typography, dark mode + dynamic color
ui/navigation/   Bottom nav route definitions
ui/pick/         The randomizer screen + PickViewModel (filtering logic)
ui/manage/       List management screen, add-item dialog, bulk-import dialog
```

## How filtering works (PickViewModel)
- `categories` — distinct `listCategory` values, live from Room.
- Selecting a category loads `itemsInCategory` via `flatMapLatest`.
- `availableTags` is derived from whatever tags actually exist on items in that category, so the chip row is always accurate and never shows stale/irrelevant tags.
- `filteredItems` combines the category's items with the selected tag set using **AND** logic (an item must have every selected tag). Change one line in `PickViewModel.filteredItems` to switch to OR logic (`tags.any { it in item.tags }`) if you'd rather match any selected tag.
- `pickRandom()` sets state to `Shuffling`, waits ~1.1s (during which the UI cycles random names for the shuffle effect), then reveals `Result(pool.random())`.

## Import format
Bulk import (Manage Lists → upload icon) accepts one item per line in two modes:
- **Plain**: `Item Name | tag1, tag2` (the `| tags` part is optional)
- **CSV**: `Item Name,tag1,tag2`

## Extending it
- **Edit existing items**: add an `updateItem` call path — `ItemRepository.updateItem` already exists, just needs a UI dialog similar to `AddItemDialog`.
- **CSV file import** (not just paste): use Storage Access Framework (`ActivityResultContracts.OpenDocument("text/csv")`) to read a `Uri`, then feed the text into the existing `importBulkText` parser.
- **Weighted random / "don't repeat last N"**: track recent picks in `PickViewModel` and filter them out of the pool before calling `.random()`.
- **Wheel-spin animation instead of text shuffle**: swap `ShufflingCard`'s content for a `Canvas` + `Animatable` rotation; the state machine (`Idle → Shuffling → Result`) doesn't need to change.
