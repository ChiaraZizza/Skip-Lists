/**
* A test suite to check functionality of SkipList class
* @author Shida Jing, Chiara Zizza, David Neil Asanza
* Class: CSC 301-01
*/

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

public class SkipListTest {

  SkipList empty;       // will be an empty SkipList
  SkipList singleton;   // will be a SkipList containing one element
  SkipList longKeyList; // will be a SkipList with very long keys
  String longKey;       // will be a very long string in the SkipList longKeyList
  int numElements;      // a constant used for testing repeated insertions/deletions

  /**
  * Initializes SkipLists, Strings, and ints declared above
  * @throws Exception
  */
  @Before
  public void setUp() throws Exception {
    numElements = 1000000;

    /* Initialize SkipLists */
    empty = new SkipList();
    singleton = new SkipList();
    longKeyList = new SkipList();

    /* Populate SkipLists */
    singleton.insert("single", "node");

    longKey = "extremely long key that never ends. Except it does. Bet you didn't see "
    + "that one coming. Can we get extra credit for creative tests?";

    longKeyList.insert("key", "key val");
    longKeyList.insert("longer key", "longer key val");
    longKeyList.insert(longKey, "longest key val");
  }

  /**
  * Test that isEmpty() function returns correct boolean for empty and
  *   non-empty SkipLists
  */
  @Test
  public void testIsEmpty() {
    assertTrue(empty.isEmpty());
    assertFalse(singleton.isEmpty());
    assertFalse(longKeyList.isEmpty());
  }

  /**
  * Test that every element of SkipLists of various lengths can be found
  * Also tests that errors are returned when an element does not exist
  *   within a SkipList
  */
  @Test
  public void testSearch() {
    /* Cannot search the empty SkipList for a key */
    try {
      empty.search("not in there");
      fail( "Key found in empty SkipList" );
    } catch (NoSuchElementException expectedException) {
    }

    /* Cannot find non-existing element */
    try {
      longKeyList.search("not in there");
      fail( "Non-inserted key found" );
    } catch (NoSuchElementException expectedException) {
    }

    /* Can find existing keys in populated SkipLists */
    assertEquals("node", singleton.search("single"));
    assertEquals("longest key val", longKeyList.search(longKey));
  }

  /**
  * Tests that insert function correctly inserted into SkipList
  */
  @Test
  public void testInsert() {
    /* singleton SkipList should contain the key, value pair "single", "node" */
    assertEquals("node", singleton.search("single"));

    /* Inserting into an empty SkipList should result in a non-empty SkipList */
    assertTrue(empty.isEmpty());
    empty.insert("key", "val");
    assertEquals("val", empty.search("key"));
    assertFalse(empty.isEmpty());
  }

  /**
  * Tests that delete function correctly deletes from SkipList
  * Also tests that errors are returned when an element does not exist
  *   within a SkipList
  */
  @Test
  public void testDelete() {
    /* Cannot delete from empty SkipList */
    try {
      empty.delete("not in there");
      fail( "Key deleted from empty SkipList" );
    } catch (NoSuchElementException expectedException) {
    }

    /* Cannot delete a non-existing key */
    try {
      longKeyList.delete("not in there");
      fail( "Deleted non-inserted key." );
    } catch (NoSuchElementException expectedException) {
    }

    /* Deleting the only element in a SkipList should yield the empty list */
    singleton.delete("single");
    assertTrue(singleton.isEmpty());
  }

  /**
  * Tests that inserting into and deleting from the front of the list
  *   works correctly
  */
  @Test
  public void testInsertSearchDeleteForward() {
    Integer medianIdx;

    SkipList test = new SkipList();
    assertTrue(test.isEmpty());

    /* Populate test SkipList */
    for (Integer i = 0; i < numElements; i++) {
      test.insert(i.toString(), i.toString());
    }

    /* Find every possible element */
    for (Integer i = 0; i < numElements; i++) {
      assertEquals(i.toString(), test.search(i.toString()));
    }

    /* Delete every element from front to back, check if other elements can
     *   still be found
     */
    for (Integer i = 0; i < numElements; i++) {
      test.delete(i.toString());
      /* Search for element in middle of SkipList */
      medianIdx = (numElements+i)/2;
      if (medianIdx > i) {
        test.search(medianIdx.toString());
      }
    }
    assertTrue(test.isEmpty());
  }

  /**
  * Tests that inserting into and deleting from the back of the list
  *   works correctly
  */
  @Test
  public void testInsertSearchDeleteBackward() {
    Integer medianIdx;

    SkipList test = new SkipList();
    assertTrue(test.isEmpty());

    /* Populate test SkipList */
    for (Integer i = numElements-1; i >= 0; i--) {
      test.insert(i.toString(), i.toString());
    }

    /* Find every possible element */
    for (Integer i = numElements-1; i >= 0; i--) {
      assertEquals(i.toString(), test.search(i.toString()));
    }

    /* Delete every element from back to front, check if other elements can
     *   still be found
     */
    for (Integer i = numElements-1; i >= 0; i--) {
      test.delete(i.toString());
      /* Search for element in middle of SkipList */
      medianIdx = i/2;
      if (medianIdx > i) {
        test.search(medianIdx.toString());
      }
    }
    assertTrue(test.isEmpty());
  }

  /**
  * Tests that inserting into and deleting from the random positions of the
  *   list works correctly
  */
  @Test
  public void testInsertSearchDeleteOutOfOrder() {
    List<String> elements = new ArrayList<>(numElements);
    Random rand = new Random();

    /* Populate elements SkipList */
    for (Integer i = 0; i < numElements; i++) {
      elements.add(i.toString());
    }

    /* Permute elements SkipList */
    Collections.shuffle(elements, rand);

    SkipList test = new SkipList();

    /* Populate elements SkipList */
    for (String key : elements) {
      test.insert(key, key);
    }

    /* Find every possible element */
    for (Integer i = 0; i < numElements; i++) {
      assertEquals(i.toString(), test.search(i.toString()));
    }

    /* Delete every element from in the order they were inserted,
    * then search for random element still in the list
    */
    for (Integer i = 0; i < numElements - 1; i++) {
      test.delete(elements.get(i));
      test.search(elements.get(i+1));
    }

    /* Delete final node in elements SkipList */
    test.delete(elements.get(numElements - 1));
    assertTrue(test.isEmpty());
  }

  /**
  * Tests that visualization of SkipList contains expected keys, and prints the
  *   visualization to the console
  */
  @Test
  public void testToString() {
    SkipList test = new SkipList();

    /* Populate test SkipList */
    for (Integer i = 0; i < 100; i++) {
      test.insert(i.toString(), "val");
    }

    String str = test.toString();

    /* Check that every key is printed */
    for (Integer i = 0; i < 100; i++) {
      assertTrue(str.contains(i.toString()));
    }

    /* Print the test list (see console for output) */
    System.out.println(test);
  }
}
