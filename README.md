# memoize-lib
Library for memozing function results.

This was created as a way to create expensive `.toString()` results on demand as needed and allow them to be garbage collected as soon as possible. The implemenation uses a `WeakReference` to hold on to the result that is being memoized so that it can be released as soon as possible and to ensure that no circular references end up causing memory leaks.

It can be used to memoize any other types that may be expensive in time and/or space as well.

```
private final Memoizer<Person,String> toString;
```
Inside the constructor you use this code to initialize it.
```
 = Memoizers.ToString(new Function<Person,String>(){
	public final String apply(@Nonnull final Person person) {
		// calculate String representation and return it
	}
	},this);
```
Then you implement `.toString()` as:
```
@Override
public String toString() { this.toString.get(); }
```
