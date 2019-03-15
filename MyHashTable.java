package hashMap;
import java.util.ArrayList;
import java.util.Iterator;

public class MyHashTable<K, V> {
	/*
	 *   Number of entries in the HashTable. 
	 */
	private int entryCount = 0;

	/*
	 * Number of buckets. The constructor sets this variable to its initial value,
	 * which eventually can get changed by invoking the rehash() method.
	 */
	private int numBuckets;

	/**
	 * Threshold load factor for rehashing.
	 */
	private final double MAX_LOAD_FACTOR=0.75;

	/**
	 *  Buckets to store lists of key-value pairs.
	 *  Traditionally an array is used for the buckets and
	 *  a linked list is used for the entries within each bucket.   
	 *  We use an Arraylist rather than an array, since the former is simpler to use in Java.   
	 */

	ArrayList< HashLinkedList<K,V> >  buckets;

	/* 
	 * Constructor.
	 * 
	 * numBuckets is the initial number of buckets used by this hash table
	 */

	MyHashTable(int numBuckets) {

		//initialize numBuckets,buckets
		this.numBuckets=numBuckets;
		this.buckets=new ArrayList<HashLinkedList<K,V>>();
		for(int i=0;i<numBuckets;i++){
			buckets.add(new HashLinkedList<K,V>());
		}

	}

	/**
	 * Given a key, return the bucket position for the key. 
	 */
	private int hashFunction(K key) {

		return  Math.abs( key.hashCode() ) % numBuckets ;
	}

	/**
	 * Checking if the hash table is empty.  
	 */

	public boolean isEmpty()
	{
		if (entryCount == 0)
			return true;
		else
			return(false);
	}

	/**
	 *   return the number of entries in the hash table.
	 */

	public int size()
	{
		return(entryCount);
	}

	/**
	 * Adds a key-value pair to the hash table. If the load factor goes above the 
	 * MAX_LOAD_FACTOR, then call the rehash() method after inserting. 
	 * 
	 *  If there was a previous value for the given key in this hashtable,
	 *  then overwrite it with new value and return the old value.
	 *  Otherwise return null.   
	 */

	public  V  put(K key, V value) {
		
		
		//this.entryCount++;
		//get position of linkedList
		int hash=hashFunction(key);
		//get the list
		HashLinkedList<K,V> positionList=buckets.get(hash);
		//If there was a previous value for the given key in this hashtable,
		//then overwrite it with new value and return the old value.
		V remove=null;
		if(positionList.getListNode(key)!=null){
			remove=positionList.remove(key).getValue();
			positionList.add(key, value);
			return remove;
		}
		//else add pair into list
		positionList.add(key, value);
		this.entryCount++;
		// If the load factor goes above the MAX_LOAD_FACTOR,
		//call the rehash() method after inserting
		if(((double)this.entryCount) / this.numBuckets > this.MAX_LOAD_FACTOR){
			this.rehash();
		}
		
		return null;
	}

	/**
	 * Retrieves a value associated with some given key in the hash table.
     Returns null if the key could not be found in the hash table)
	 */
	public V get(K key) {

		
		int hash=this.hashFunction(key);
		//Returns null if the key could not be found in the hash table
		if(buckets.get(hash)==null){
			return null;
		}else{
			HashLinkedList<K,V> positionList=this.buckets.get(hash);
			if(positionList.getListNode(key)==null){
				return null;
			}else{
				//get value at key position
				HashNode<K,V> current=positionList.getListNode(key);
				V val=current.getValue();
				return val;
			}
		}
		
	}

	/**
	 * Removes a key-value pair from the hash table.
	 * Return value associated with the provided key.   If the key is not found, return null.
	 */
	public V remove(K key) {

		
		int hash=this.hashFunction(key);
		//Return value associated with the provided key.   
		//If the key is not found, return null
		HashLinkedList<K,V> positionList = this.buckets.get(hash);
		HashNode<K,V> node=positionList.getListNode(key);
		if(node==null){
			return null;
		}else{
			HashNode<K,V> test=positionList.remove(key);
			//remove pair at key
			this.entryCount--;
			return node.getValue();
		}
		
	}

	/*
	 *  This method is used for testing rehash().  Normally one would not provide such a method. 
	 */

	public int getNumBuckets(){
		return numBuckets;
	}

	/*
	 * Returns an iterator for the hash table. 
	 */

	public MyHashTable<K, V>.HashIterator  iterator(){
		return new HashIterator();
	}

	/*
	 * Removes all the entries from the hash table, 
	 * but keeps the number of buckets intact.
	 */
	public void clear()
	{
		for (int ct = 0; ct < buckets.size(); ct++){
			buckets.get(ct).clear();
		}
		entryCount=0;		
	}

	/**
	 *   Create a new hash table that has twice the number of buckets.
	 */


	public void rehash()
	{
		
			//make the iterator and clear all old values
			HashIterator iterator = this.iterator();
			this.clear();
			for(int i = 0; i < this.numBuckets; i++){
				this.buckets.add(new HashLinkedList<K,V>());
			}
			//twice the size of numBuckets
			this.numBuckets=2*this.numBuckets;
			//add all value back
			while(iterator.hasNext()){
				HashNode<K,V>node = iterator.next();
				this.put(node.getKey(), node.getValue());
			} 
		
	}

	/*
	 * Checks if the hash table contains the given key.
	 * Return true if the hash table has the specified key, and false otherwise.
	 */

	public boolean containsKey(K key)
	{
		int hashValue = hashFunction(key);
		if(buckets.get(hashValue).getListNode(key) == null){
			return false;
		}
		return true;
	}

	/*
	 * return an ArrayList of the keys in the hashtable
	 */

	public ArrayList<K>  keys()
	{

		ArrayList<K>  listKeys = new ArrayList<K>();

		for(int i=0;i<numBuckets;i++){
			HashLinkedList<K,V> positionList=buckets.get(i);
			if(positionList!=null){
				HashNode<K,V> cur=positionList.getHead();
				while(cur!=null){
					listKeys.add(cur.getKey());
					cur=cur.getNext();
				}
			}
		}
		return listKeys;   
	}

	/*
	 * return an ArrayList of the values in the hashtable
	 */
	public ArrayList <V> values()
	{
		ArrayList<V>  listValues = new ArrayList<V>();

		for(int i=0;i<numBuckets;i++){
			HashLinkedList<K,V> positionList=buckets.get(i);
			if(positionList!=null){
				HashNode<K,V> cur=positionList.getHead();
				while(cur!=null){
					listValues.add(cur.getValue());
					cur=cur.getNext();
				}
			}
		}
		return listValues; 
	}

	@Override
	public String toString() {
		/*
		 * Implemented method. 
		 */
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buckets.size(); i++) {
			sb.append("Bucket ");
			sb.append(i);
			sb.append(" has ");
			sb.append(buckets.get(i).size());
			sb.append(" entries.\n");
		}
		sb.append("There are ");
		sb.append(entryCount);
		sb.append(" entries in the hash table altogether.");
		return sb.toString();
	}

	/*
	 *    Inner class:   Iterator for the Hash Table.
	 */
	public class HashIterator implements  Iterator<HashNode<K,V> > {
		HashLinkedList<K,V>  allEntries;

		
		public  HashIterator()
		{
			this.allEntries=new HashLinkedList<K,V>();
			for(int i=0;i<numBuckets;i++){
				HashLinkedList<K,V> list=buckets.get(i);
				HashNode<K,V> listNode = list.getHead();
				while(listNode != null){
					allEntries.add(listNode.getKey(), listNode.getValue());
					listNode = listNode.next;

				}
			}

		}

		//  Override
		@Override
		public boolean hasNext()
		{
			return !allEntries.isEmpty();
		}

		//  Override
		@Override
		public HashNode<K,V> next()
		{
			return allEntries.removeFirst();
		}

		@Override
		public void remove() {
			// not implemented,  but must be declared because it is in the Iterator interface

		}		
	}

}
