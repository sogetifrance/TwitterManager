/* Copyright (c) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sogeti;

import com.google.appengine.api.ThreadManager;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;

import java.util.Random;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;

/**
 * This initial implementation simply counts all instances of the
 * SimpleCounterShard kind in the datastore. The only way to increment the
 * number of shards is to add another shard by creating another entity in the
 * datastore.
 */
public class TwitterManagerService {

  private static final DatastoreService ds = DatastoreServiceFactory
      .getDatastoreService();

  /**
   * Default number of shards.
   */
  private static final int NUM_SHARDS = 20;

  /**
   * A random number generator, for distributing writes across shards.
   */
  private final Random generator = new Random();

  /**
   * Retrieve the value of this sharded counter.
   * 
   * @return Summed total of all shards' counts
   */
  public long getCount() {
    long sum = 0;
    Query query = new Query("SimpleCounterShard");
    for (Entity e : ds.prepare(query).asIterable()) {
      sum += (Long) e.getProperty("count");
    }

    return sum;
  }

  public void increment() {
	  System.out.println("entree methode increment");
	  try {
			Runnable manage = new Runnable() {
				public void run() {
					incrementRunnable();
				}
			};

			ThreadFactory threadFactory = ThreadManager
					.backgroundThreadFactory();
			Thread thread = threadFactory.newThread(manage);
			thread.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getCause() + e.getMessage());
		}
	System.out.println("sortie methode increment");
  }
  
  /**
   * Increment the value of this sharded counter.
   */
  public void incrementRunnable() {
	 System.out.println("entree methode incrementRunnable");
	 try {
		Thread.sleep(610000);
	} catch (InterruptedException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
    int shardNum = generator.nextInt(NUM_SHARDS);
    Key shardKey = KeyFactory.createKey("SimpleCounterShard",
        Integer.toString(shardNum));

    Transaction tx = ds.beginTransaction();
    Entity shard;
    try {
      shard = ds.get(tx, shardKey);
      long count = (Long) shard.getProperty("count");
      shard.setUnindexedProperty("count", count + 1L);
    } catch (EntityNotFoundException e) {
      shard = new Entity(shardKey);
      shard.setUnindexedProperty("count", 1L);
    }
    ds.put(tx, shard);
    tx.commit();
    
    System.out.println("sortie methode incrementRunnable");
  }
}
