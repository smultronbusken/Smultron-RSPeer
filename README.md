# Smultron RSPeer

Just a personal project where I try to make some kind of framework for building a task based script with a lot of tasks already implemented. Performance is not the goal rather than a script which can do a lot of things. Main focus is tree tasks. 

This framework is built using the [RSpeer](https://rspeer.org/) API.

# Example
Heres how I would make a very basic script which chops wood at lumbridge. It drops the logs when the inventory is full.

This is the actual script class with the ScriptMeta annotation.
The method nextTask() decides which task the script should execute next.

```java
@ScriptMeta(desc = "playing around", developer = "smultron", name = "Playground")
public class Playground extends MullbarScript {
    @Override
    public Task nextTask() {
        return new ChopAndBank();
    }
}
```

Aaaand heres the task:
```java
public class ChopAndBank extends TreeTask {

    private final static Area TREE_AREA = Area.rectangular(3198, 3245, 3205, 3238);
    private final static Supplier<SceneObject> TREE_SUPPLIER = () -> SceneObjects.getNearest("Tree");
    
    public ChopAndBank() {
        super("Chopping trees in Lumbridge");
    }
    
    @Override
    public TreeNode onCreateRoot() {
        Task chopTree = new InteractWith<>("Chop down", TREE_SUPPLIER);
       
        // Will walk to the area if were not there, then execute chopTree. 
        TreeNode atTreeArea = new InArea(chopTree, Location.location(TREE_AREA, "the tree area"), 5);
        
        // A FunctionalTask is useful for small tasks such as this
        Task dropInventory = new FunctionalTask(() -> {
            for(Item log : Inventory.getItems(item -> item.getName().equals("Logs"))){
                log.interact("Drop");
                Time.sleep(Random.nextInt(200, 900));
            }
        };
    
        // Create a binary branch with the condition if the inventory is full
        TreeNode isInventoryFull = BinaryBranchBuilder.getNewInstance()
                .successNode(dropInventory) // Execute this task if Inventory::isFull returns true
                .setValidation(Inventory::isFull)
                .failureNode(atTreeArea) // Execute this task if Inventory::isFull returns false
                .build();
        
        return isInventoryFull;
    }
    
    @Override
    public boolean validate() {
        // The task will run forever
        return false;
    }
}
````
