# Diagrammer

Dette dokument indeholder alle diagrammer der er blevet lavet i projektet.

## Domænemodel

```mermaid
classDiagram 
direction LR

class User {
	
}

class Inventory {
	Slots
	Available slots
}

class Slot {
	Item
	Number of items
}

class Item {
	Template of item
	Stats for an item
}

class ItemTemplate {
	Id
	Name
	Consumable
	Weight
	Max stack size
}

User "1" -- "1..n" Inventory : Manages
Inventory "1" -- "192" Slot : Has
Slot "1" -- "1" Item : Has
Item "1" -- "1" ItemTemplate : References
```

## Systemsekvensdiagram

```mermaid
sequenceDiagram

participant gui as GUI
participant gis as Gaming Inventory System
participant itemManager as Item Manager
participant db as Database

gui ->> gis: Detaljer om inventory
activate gis
gis ->> db: Få alle slots
activate db
db -->> gis: Alle inventory slots
deactivate db
gis ->> itemManager: Få alle item detaljer
activate itemManager
itemManager -->> gis: Alle item detaljer
deactivate itemManager
gis -->> gui: Inventory slots og detaljer
deactivate gis
```


```mermaid
sequenceDiagram

participant gui as GUI
participant gis as Gaming Inventory System
participant itemManager as Item Manager
participant db as Database

gui ->> gis: Få specifikt slot i inventory
activate gis
gis ->> db: Slot i inventory
activate db
db -->> gis: Item i slot
deactivate db
gis ->> itemManager: Få item detaljer
activate itemManager
itemManager -->> gis: Item detaljer
deactivate itemManager
gis -->> gui: Inventory slot og item detaljer
deactivate gis
```

## Hele inv

```mermaid
sequenceDiagram

actor gui as User
participant gis as Gaming Inventory System

gui ->> gis: Få hele inventory
activate gis
gis -->> gui: Inventory detaljer (vægt, antal, total osv.) og slots
deactivate gis
```

## Enkelt slot

```mermaid
sequenceDiagram

actor gui as User
participant gis as Gaming Inventory System

gui ->> gis: Få specifikt slot i inventory
activate gis
gis -->> gui: Inventory slot og item detaljer
deactivate gis
```

## Import

```mermaid
sequenceDiagram

actor gui as User
participant gis as Gaming Inventory System

gui ->> gis: Import inventory
activate gis
gis -->> gui: Confirm action
deactivate gis
```

## Export

```mermaid
sequenceDiagram

actor gui as User
participant gis as Gaming Inventory System

gui ->> gis: Export inventory
activate gis
gis -->> gui: Confirm action
deactivate gis
```


## Klassediagram

```mermaid
classDiagram
	direction LR

	class DatabaseManager {
		-Connection connection$
		+DatabaseManager(String file)
		+void migrate()
		+Connection getInstance()
	}

	class ItemTemplateManager {
		-List<ItemTemplate> itemTemplates
		+void addItemTemplate(Item template)
		+void removeItemTemplate(Item template)
		+ItemTemplate findById(int id)
		+ItemTemplate findByName(String name)
	}
	
	class InventoryManager {
		-List<Inventory> inventories
		+Inventory newInventory()
		+Inventory getInventory(int id)
	}
	
	class Inventory {
		-int id
		-Slot[192] slots
		-int unlockedSlots
	
		+void addItem(Item item)
		+void searchSlots(String search)
		+void clearSlot(int slotNumber)
		+double weight()
	}
	
	class Slot {
		-Item item
		-int count
	
		+double weight()
	}
	
	class Item {
		<<abstract>>
		-int id
		-ItemType type
		-String name
		-double weight
		-int maxStack
		-bool consumable
		
		+void use()
		+double weight()
	}
	
	class WeaponItem {
		-double damage
		-int durability
		+void use()
	}
	
	
	class ArmorItem {
		-double defense
		-int durability
		+void use()
	}
	
	class HealingPotionItem {
		-double healing
		-int uses
		+void use()
	}
	
	class ItemType {
		<<enumerable>>
		Weapon,
		Armor,
		Potion
	}
	
	InventoryManager --> Inventory
	ItemTemplateManager --> Item
	
	Inventory --> Slot
	Slot --> Item
	Item --> ItemType
	
	WeaponItem --|> Item
	ArmorItem --|> Item
	HealingPotionItem --|> Item
```


## Sekvensdiagram 1 Set slot manually

```mermaid
sequenceDiagram
	participant input as 
	participant gui as :Gui
	participant inv as :Inventory
	participant slot as :Slot
	participant item as :Item
	participant itemManager as instance:ItemManager

	input ->> gui: Manage slot
	gui ->> inv: getSlot(int index)
	inv -->> gui: Slot
	gui ->> itemManager: getItems()
	itemManager -->> gui: List of items
	gui ->> slot: setItem(Item item)
	slot -->> gui: Slot item set
	gui ->> slot: setCount(int count)
	slot -->> gui: Slot item count set
	gui -->> input: Slot managed
```


## Sekvensdiagram: List all items

```mermaid
sequenceDiagram
	participant input as 
	participant gui as :Gui
	participant inv as :Inventory
	participant slot as :Slot
	participant item as :Item
	participant itemManager as instance:ItemManager

	input ->> gui: Get item list
	gui ->> itemManager: getItems()
	itemManager -->> gui: List of items
	loop list
	gui ->> item: getName()
		item -->> gui: Item names
	end
	gui -->> input: Show item list
```

## Sekvensdiagram: Add item to inventory

```mermaid
sequenceDiagram
	participant input as 
	participant gui as :Gui
	participant inv as :Inventory
	participant slot as :Slot

	input ->> gui: Selected item and add to inventory
	gui ->> inv: addItem()
	inv ->> slot: setItem()
	slot -->> inv: Item set
	inv ->> slot: setCount()
	slot -->> inv: Item count set
	inv -->> gui: Item added
	gui -->> input: Item added
```


## Sekvensdiagram: Vælg specifikt slot

```mermaid
sequenceDiagram
	participant input as 
	participant gui as :Gui
	participant inv as :Inventory
	participant slot as :Slot
	participant item as :Item

	input ->> gui: Select slot
	gui ->> inv: getSlots()
	inv -->> gui: Array of slots
	gui ->> item: getName()
	item -->> gui: Name of item
	gui ->> item: getId()
	item -->> gui: Id of item
	gui ->> slot: getCount()
	slot -->> gui: Count of items in slot
	gui ->> slot: getWeight()
	slot -->> gui: Weight of slot
	gui -->> input: Slot stats and options
```

## Klassediagram

```mermaid
classDiagram
	class Main {
		+main(String[] args)$
		-createFileRepository(FileRepository fileRepo, String dataFile) FileRepository$
	}

	Main ..> Gui
	Main ..> FileRepository
	Main ..> Item
	Main ..> WeaponItem
	Main ..> ConsumablePotion

	class Gui {
		+Gui(InventorySystemRepository repository)
		-InventorySystemRepository repository
		-Scanner scanner
		+void start()
		-Inventory createInventoryPrompt
		-Inventory selectInventoryPrompt(List~Inventory~ inventories)
		-manageInventoryPrompt(Inventory inventory)
		-printInventoryStats(Inventory inventory)
		-printInventorySlots(Inventory inventory, int offset) int
		-manageSlotsPrompt(Inventory inventory)
		-sortSlots(Inventory inventory)
		-manageSlot(Inventory inventory, int slotIndex)
		-insertItemToSlot(Inventory inventory, int slotIndex)
		-swapSlots(Inventory inventory, int slot1)
		-readOption(int min, int max) int
		-readOption(int[] allowed) int
		-readOption(Scanner scanner, int min, int max) int$
		-readOption(Scanner scanner, int[] allowed) int$
	}

	Gui "1" --> "1" InventorySystemRepository
	Gui ..> ItemManager
	Gui ..> Inventory 

	class InventorySystemRepository {
		<<Interface>>
		getInventories() List~Inventory~
		newInventory(String name, int unlockedSlots) Inventory
		void save()
		void addInventory(Inventory inventory)
		void removeInventory(Inventory inventory)
	}

	class FileRepository {
		-String fileName
		-InventoryFile inventoryFile
		+FileRepository(String fileName)
		-readInventoryFile() InventoryFile 
		+getInventories() List~Inventory~
		+newInventory(String name, int unlockedSlots) Inventory
		+save()
		+addInventory(Inventory inventory)
		+removeInventory(Inventory inventory)
	}

	FileRepository ..|> InventorySystemRepository
	FileRepository "1" --> "1" InventoryFile

	class InventoryFile {
		-List~Inventory~ inventories
		+InventoryFile()
		-addInventory(Inventory inventory)
		-removeInventory(Inventory inventory)
		+getInventories() List~Inventory~
		+inventoryCount() int
	}

	InventoryFile "1" --> "0..*" Inventory

	class Inventory {
		-String name
		-Slot[] slots
		-int unlockedSlots
		-double MAX_WEIGHT$
		-Inventory()
		+Inventory(String name, int unlockedSlots)
		+getName() String
		+setName(String name)
		+getUnlockedSlots() int
		+setUnlockedSlots(int unlockedSlots)
		+getSlots() Slot[]
		+getSlot(int index): Slot
		+getWeight() double
		+insertToSlot(int slotIndex, Item item, int count)
		+incrementSlot(int slotIndex)
		+decrementSlot(int slotIndex)
		+clearSlot(int slotIndex)
		+sortInventory(SortValue sort)
		+swapSlots(int slot1, int slot2)
		+writeToFile(Writer w)
		+fromFile(File file) Inventory$
	}
	
	Inventory "1" --> "192" Slot
	Inventory ..> SortValue

	class SortValue {
		<<Enumeration>>
		Id
		Alphabetical
		ItemType
		Weight
	}

	class Slot {
		-Item item
		-int count
		+Slot()
		+setItem(Item item)
		+getItem() Item		
		+setCount(int count)
		+getCount() int
		+getWeight() double
		+isEmpty() boolean
		+isNotEmpty() boolean
		+incrementCount()
		+decrementCount()
		+use()
		+clear()
	}

	Slot "1" --> "1" Item

	class ItemType {
		<<Enumeration>>
		Armor
		ConsumablePotion
		ThrowingWeapon
	}
	
	class Item {
		<<Abstract>>
		-int id
		-ItemType type
		-String name
		-double weight
		-int maxStack
		#Item()
		+Item(int id, ItemType type, String name, double weight, int maxStack)
		#use()*
		+getId() int
		+setId(int id)
		+getType() ItemType
		+setType(ItemType type)
		+getName()
		+setName(String name)
		+getWeight() double
		+setWeight(double weight)
		+getMaxStack() int
		+setMaxStack(int maxStack)
	}

	Item "1" --> "1" ItemType

	class ItemManager {
		-ItemManager instance$
		-List~Item~ items
		+getInstance() ItemManager$
		+insertItem(Item item)
		+getItems() List~Item~
	}

	ItemManager --> Item

	class WeaponHandedness {
		MainHand
		Offhand
		BothHands
		TwoHand
	}
	
	class ArmorItem {
		+ArmorItem()
		+ArmorItem(int id, String name, double weight, int maxStack)
		#use()
	}

	ArmorItem --|> Item

	class ConsumablePotion {
		+ConsumablePotion()
		+ConsumablePotion(int id, String name, double weight, int maxStack)
		#use()
	}

	ConsumablePotion --|> Item

	class WeaponItem {
		-WeaponHandedness weaponHandedness
		-double damage
		-int durability
		+WeaponItem()
		+WeaponItem(int id, String name, double weight, int maxStack, WeaponHandedness weaponhandedness, double damage, int durability)
		#use()
	}
	
	WeaponItem --|> Item
	WeaponItem "1" --> "1" WeaponHandedness
	
	class ThrowingWeapon {
		-WeaponHandedness weaponHandedness
		-double damage
		-int durability
		-double throingDistance
		+ThrowingWeapon()
		+ThrowingWeapon(int id, String name, double weight, int maxStack, WeaponHandedness weaponhandedness, double damage, int durability, double throwingDistance)
		#use()
	}
	
	ThrowingWeapon --|> Item
	ThrowingWeapon "1" --> "1" WeaponHandedness
```
