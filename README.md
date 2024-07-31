# pquery

A simple querying language for the JVM, written in Kotlin. Designed to be extensible and simple to use.

All functionality in pquery is implemented via plugins, which allow you to define:
- Variables (values that will resolve into booleans, numbers, or strings when called)
- Logical operators (`||` for logical or, `&&` for logical and, etc.)
- Comparison operators (`==` for 'equal to', `>=` for 'greater or equal to', etc.)
- Mathematical operators (`+`, `/`, `^`, etc.)

Even the very basic operators are implemented using the `base` plugin, available by default.

All queries must resolve into a boolean value. Queries can be compiled once, and called/evaluated as many times as you want. When a query is evaluated, all variables are also evaluated at the same time. This means the same compiled query can return a different value over time, as conditions may change (etc, a variable that reports the system time).

## Sample queries

Most queries should be very easy to understand if you have any experience with programming languages.

For those unfamiliar (if you have a programming background in only Python, Lua, or similar):
- `&&` means `and`
- `||` means `or`
- `!=` means `not equal`

```
true == true
```
_(evaluates to `true`)_

```
5 >= 2
```
_(evaluates to `true`)_

```
(10 + 4 / 2) != 7
```
_(evaluates to `true`)_

```
"cheerleader" == "musician" && "flicker" == "flicker"
```
_(evaluates to `false`)_

```
minecraft.player.coords.y >= 60
```
_(assuming a Minecraft plugin is provided, will evaluate to `true` is the player's Y level is greater or equal to 60, or `false` otherwise)_

## Installation

Currently, this project is not available on any repository. I plan on uploading it to one once I add some better docs, add a few more missing features, and figure out the Maven Central requirements, lol

If you'd like to use it anyway, I would recommend downloading the source code and setting Gradle to publish it to your own private repo, or Maven Local.

## Usage

There is mainly one way to use pquery, through the `pquery` helper class, though you can also technically set up everything manually if you, for example, don't want to include the 'base' plugin (for whatever reason).

### `pquery` Helper class

You'll first want to create an instance of the `pquery` helper class. This class will automatically initialize all needed components, and comes with the `base` plugin by default.

```kotlin
val pqueryInstance = pquery()
```

You can pass a list of plugins you'd like to use into the constructor. You only need to create an instance of the helper class once, unless you want to maintain different instances with different sets of plugins.

Next, you'll want to compile a program. This function only takes in one string: the query itself. It will return a `Result<BooleanResolvable>` object, so you can handle any compilation errors however you want. The `BooleanResolvable` object encapsulated within is a compiled version of our query, which will be evaluated everytime we call it.
```kotlin
val compiledQuery = pqueryInstance.compile("1 + 2 == 3").getOrThrow() // I would recommend using .onFailure or .getOrElse to handle query errors instead. Read up on Kotlin's Result API to learn more!
```

To evaluate our query:
```kotlin
val result = compiledQuery.resolve()
println("Does 1 + 2 = 3? The answer is: $result")
```
