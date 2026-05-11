/*
 * Author: Mahika Mahapatra
 * Email: mmahapatra2@wisc.edu
 * Assignment: Program P212.Hashtable
 * Course: Compsci400 Date: 4/19/2026
 * Citations: worked on byself
 */


import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;




/**
 *class for hash table that maps keys to values using chaining (closed
 * addressing) to handle collisions
 */
public class HashTableMap<KeyType, ValueType> implements MapADT<KeyType, ValueType> {
    private int capacity;


    /**
     * class to store a key value pair together as single object
    */
    protected class Pair {
    public KeyType key;
    public ValueType value;


    /**
    * this constructor constructs a new pair bundling the given key and value together.
    * @param key is the key to store
    * @param value is the value associated with that key (may be null)
    */
    public Pair(KeyType key, ValueType value) {
        this.key = key;
        this.value = value;
    }
}


    protected LinkedList<Pair>[] table = null; // array of linked lists (chains) for storing pairs
    private int size; //num of key val pairs currently stored



    /**
    * constructs a new, empty HashTableMap with the given initial capacity,
    * backing array is created as a LinkedList[] and cast to
    * LinkedList<Pair>[]
    * @param capacity is the number of buckets to allocate
    * @throws IllegalArgumentException if capacity is less than 1
    */
    @SuppressWarnings("unchecked")
    public HashTableMap(int capacity){ //constructor

        if (capacity < 1) {
            throw new IllegalArgumentException();
        }
        this.capacity = capacity;
        this.table = (LinkedList<Pair>[]) new LinkedList[capacity]; // create array of linked lists with given capacity
        this.size = 0;

    }

    /**
    * This constructor constructs a new, empty HashTableMap with a initial capacity of 8
    */
    public HashTableMap() {
        this(8);
    }

      /**
     * This helper method converts a key into a valid bucket index
     * for the current table. Also result must be non-negative and
     * within bounds of array
     * @param key key to hash (can't be null)
     * @return an index in the range [0, capacity)
     */
    private int getIndex(KeyType key) {
        //hash key to a non-neg int,takes remainder when divided by capacity to get a valid bucket index
        return Math.abs(key.hashCode()) % capacity;
    }



    /**
    * This helper method sizes the hash table when the load factor reaches
    * at least 75% if so it doubles the capacity, and creates a new
    * array, and  also rehashes every existing pair into its new bucket
    * index using the updated capacity
    */
    @SuppressWarnings("unchecked") //stop the warning
    private void resizeIfNeeded() {

        //resize only when the load factor (LF) has reached or exceeded 75%
        if ((double) size / capacity < 0.75) { //typcast (decimal num instead of a whole num before division)
            return; //do nothing
    }

    int newCapacity = capacity * 2;
    LinkedList<Pair>[] newTable = (LinkedList<Pair>[]) new LinkedList[newCapacity];

    //get each pair's index under the new capacity
    for (int i = 0; i < capacity; i++) {
        if (table[i] == null) {
            continue;    //if empty bucket —> nothing to move, go to next iterartion of loop
        }

        for (int j = 0; j < table[i].size(); j++) {
            Pair p = table[i].get(j);
            int newIndex = Math.abs(p.key.hashCode()) % newCapacity;

            // initialize  the destination bucket on first use
            if (newTable[newIndex] == null) {
                newTable[newIndex] = new LinkedList<>();
            }

            newTable[newIndex].add(p); //moveexisting pair object
        }
    }
    table    = newTable; //new array
    capacity = newCapacity; //updated capacity
}


    /**
     * Adds a new key,value pair/mapping to this collection.
     * @param key the key of the key,value pair
     * @param value the value that key maps to (may be null)
     * @throws IllegalArgumentException if key already maps to a value without
     *         making any changes to the table
     * @throws NullPointerException if key is null
    */
    @Override
    public void put(KeyType key, ValueType value) throws IllegalArgumentException {
        if (key == null) { //null check
            throw new NullPointerException("key can't be null");
        }
        if (containsKey(key)) {
            throw new IllegalArgumentException("the key already exists");
        }
        int index = getIndex(key); //save result into variable

        // create linkedlist first time key lands here
        if (table[index] == null) {
            table[index] = new LinkedList<>(); //if bucket is null(bucket not used yet) create linked list
        }

        table[index].add(new Pair(key, value)); //take key and value, put into one pair object, then add pair to  LinkedList at correct bucket
        size++; //inc. size counter by 1

        resizeIfNeeded(); //grow table if LF has reached  75% or more
    }

    /**
     * Checks whether a key maps to a value in this collection.
     * @param key the key to check
     * @throws NullPointerException if key is null
     * @return true if the key maps to a value, and false is the key doesn't
     *         map to a value
    */
    @Override
    public boolean containsKey(KeyType key) {
        if (key == null) { //null check
            throw new NullPointerException("key can't be null");
        }

        int index = getIndex(key); //save result into variable

         if (table[index] == null) { //if  bucket is never used, key cannot be here
            return false;
        }
        for (int j = 0; j < table[index].size(); j++) {
            Pair p = table[index].get(j); // get the pair at position j in the chain
            if (p.key.equals(key)) {
                 return true; // found a match, key exists in table
            }
        }
        return false;
}

    /**
     * Retrieves the specific value that a key maps to.
     * @param key the key to look up
     * @return the value that key maps to
     * @throws NoSuchElementException when key is not stored in this collection
     * @throws NullPointerException if key is null
    */
    @Override
    public ValueType get(KeyType key) throws NoSuchElementException {
        if (key == null) { //null check
            throw new NullPointerException("key can't be null");
        }

        int index = getIndex(key); //save result into variable

        if (table[index] != null) {
            for (int j = 0; j < table[index].size(); j++) { // iterate thru each pair in the  linked list

                Pair p = table[index].get(j); // get the pair at position j in chain
                if (p.key.equals(key)) {
                    return p.value; // found matching key, return its value
                }
            }
        }
        //key not in any bucket,throw exception :(
        throw new NoSuchElementException("key is not found ");

    }


    /**
     * Remove the mapping for a key from this collection.
     * @param key the key whose mapping to remove
     * @return the value that the removed key mapped to
     * @throws NoSuchElementException when key is not stored in this collection
     * @throws NullPointerException if key is null
    */
    @Override
    public ValueType remove(KeyType key) throws NoSuchElementException {
        if (key == null) { //null check
            throw new NullPointerException("key can't be null");
        }

        int index = getIndex(key); //save result into variable

        if (table[index] != null) {
            for (int j = 0; j < table[index].size(); j++) { // iterate thru each pair in the bucket's chain (linkedlist)
                Pair p = table[index].get(j); // get the pair at position j in the chain

                if (p.key.equals(key)) { // found matching key
                    ValueType removed = p.value; // save the value to return it later
                    table[index].remove(j); // remove the pair at position j from the chain
                    size--; // decrement size since one pair was removed
                    return removed; // return the value that was removed
                }
            }
        }

        //key not in any bucket,throw exception :(
        throw new NoSuchElementException("key is not found ");


}


    /**
     * Removes all key,value pairs from this collection without changing the
     * capacity of the underlying array.
    */
    @Override
    public void clear() {
         for (int i = 0; i < capacity; i++) {
            table[i] = null; // after nothing points to linked list, garbage collector cleans up
        }
        size = 0;//set size to 0, bc cleared

    }

    /**
     * Retrieves the number of keys stored in this collection.
     * @return the number of keys stored in this collection
    */
    @Override
    public int getSize() {
        return size; //just return size
    }

    /**
     * Retrieves this collection's capacity.
     * @return the size of the underlying array for this collection
    */
    @Override
    public int getCapacity() {
        return capacity; //just return capacity
    }
    /**
     * Retrieves this collection's keys.
     * @return a list of keys in the underlying array for this collection
    */
    @Override
    public List<KeyType> getKeys() {
        List<KeyType> keys = new LinkedList<>();

        for (int i = 0; i < capacity; i++) {
            if (table[i] == null) {
                continue; // skip empty buckets
            }
            for (int j = 0; j < table[i].size(); j++) { // iterate thru each pair in the bucket's chain (linkedlist)
                keys.add(table[i].get(j).key); // get pair at position j and add its key to the list
            }
        }
        return keys;

    }
    /**
     * This test checks that the put and get operations work as they should
     * and that getKeys correctly retrieves the stored keys.
     */
    @Test
    public void testPutAndGet() {
        HashTableMap<Integer, String> map = new HashTableMap<>(8);

        // add 2 keys that hash to the same index (1%8 = 9%8)
        map.put(1, "Value1");
        map.put(9, "Value9");

        // check that we can get both values correctly
        assertEquals("Value1", map.get(1));
        assertEquals("Value9", map.get(9));

        assertEquals(2, map.getSize()); // check size is 2

        // also check getKeys returns the expected keys
        List<Integer> keys = map.getKeys(); //get list of all keys currently stored in the hash table
        assertTrue(keys.contains(1)); //check that the list contains the 1st we inserted
        assertTrue(keys.contains(9)); //cehack that the list contains the 2ndkey we inserted
        assertEquals(2, keys.size()); // make sure size of the list matches the expected number of keys map
    }

    /**
     * This test checks that containsKey works correctly for  both present and not presnet keys
     */
    @Test
    public void testContainsKey() {
        HashTableMap<String, String> map = new HashTableMap<>(8); //create map with capacity of 8

        map.put("A", "Apple"); /// single key value pair to test function works with presnt key

        assertTrue(map.containsKey("A")); //should contain key A
        assertFalse(map.containsKey("B"));  //should not contain key B as key not present
    }

    /**
     * This test checks that  the remove function correctly removes an item and updates the size
     */
    @Test
    public void testRemove() {
        HashTableMap<Integer, String> map = new HashTableMap<>(8); //create map with capacity of 8

        map.put(1, "One"); // key value pair to check if removal function works

        assertEquals("One", map.remove(1)); //check return value one
        assertEquals(0, map.getSize()); // check size should be 0 after removal
        assertFalse(map.containsKey(1) ); // check the key should no longer be in map
    }

   /**
     * This test checks that all data is correct after resize and rehash operation has occurred
     */
    @Test
    public void testRehashConsistency() {

        HashTableMap<Integer, String> map = new HashTableMap<>(8); //create map with capacity of 8

        //  add 6 key value pair to checkto force a resize as load factor would be 75% (6/8)
        map.put(1, "Val1");
        map.put(2, "Val2");
        map.put(3, "Val3");
        map.put(4, "Val4");
        map.put(5, "Val5");
        map.put(6, "Val6");

        // check that all 6 elements are still actually accessible after the resize function is called
        assertEquals("Val1", map.get(1));
        assertEquals("Val2", map.get(2));
        assertEquals("Val3", map.get(3));
        assertEquals("Val4", map.get(4));
        assertEquals("Val5", map.get(5));
        assertEquals("Val6", map.get(6));
        assertEquals(16, map.getCapacity()); // check that capacity  doubled from 8 to 16
    }

    /**
     * This test verifies that the clear method removes all elements and resets size to 0
     * after the table has gone through a resize operation
     */
    @Test
    public void testClearAfterResize() {
        HashTableMap<Integer, String> map = new HashTableMap<>(8); //create map with capacity of 8
        // add 8 key value pair to cause resize method to run (8/8-> load factor = 1)
        map.put(1, "Val1");
        map.put(2, "Val2");
        map.put(3, "Val3");
        map.put(4, "Val4");
        map.put(5, "Val5");
        map.put(6, "Val6");
        map.put(7, "Val7");
        map.put(8, "Val8");



         int capBefore = map.getCapacity(); // save capacity before clear
         map.clear(); //clear the map

         // check size is reset and map is empty after clear
         assertEquals(0, map.getSize()); //size should be 0 after clear
         assertFalse(map.containsKey(1)); //key 1 should no longer be present
         assertEquals(capBefore, map.getCapacity()); //capacity must not change after clear
    }
}





















