## Notes

Some of the classes have private variable of type ``java.util.Random``.
 
It's not defined as static, because if we work in multi-threaded environment, 
it will reduce time efficiency, since it's implemented with use of ``synchronization``

See. from javadoc: 
```
 * <p>Instances of {@code java.util.Random} are threadsafe.
 * However, the concurrent use of the same {@code java.util.Random}
 * instance across threads may encounter contention and consequent
 * poor performance. Consider instead using
 * {@link java.util.concurrent.ThreadLocalRandom} in multithreaded
 * designs.
```