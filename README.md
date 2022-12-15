## { Advent of code 2022 } -> Kotlin ⛄

My kotlin solutions for Advent of code 2022

Easy: 🟢, Medium: 🟡, Challenging: 🟣, Hard: 🔴

| Day |            Title            |                                                                                                                       Links | Difficulty |
|-----|:---------------------------:|----------------------------------------------------------------------------------------------------------------------------:|------------|
| 1   |     Calorie Counting 🍪     |  [{Aoc}](https://adventofcode.com/2022/day/1)  [[Code]](https://github.com/Gers2017/aoc-2022-kotlin/blob/main/src/Day01.kt) | 🟢         |
| 2   |   Rock Paper Scissors ✌️    |  [{Aoc}](https://adventofcode.com/2022/day/2)  [[Code]](https://github.com/Gers2017/aoc-2022-kotlin/blob/main/src/Day02.kt) | 🟡         |
| 3   | Rucksack Reorganization 🎒  |  [{Aoc}](https://adventofcode.com/2022/day/3)  [[Code]](https://github.com/Gers2017/aoc-2022-kotlin/blob/main/src/Day03.kt) | 🟢         |
| 4   |       Camp Cleanup ⛺        |  [{Aoc}](https://adventofcode.com/2022/day/4)  [[Code]](https://github.com/Gers2017/aoc-2022-kotlin/blob/main/src/Day04.kt) | 🟢         |
| 5   |      Supply Stacks 📦       |  [{Aoc}](https://adventofcode.com/2022/day/5)  [[Code]](https://github.com/Gers2017/aoc-2022-kotlin/blob/main/src/Day05.kt) | 🟡         |
| 6   |      Tuning Trouble 📜      |  [{Aoc}](https://adventofcode.com/2022/day/6)  [[Code]](https://github.com/Gers2017/aoc-2022-kotlin/blob/main/src/Day06.kt) | 🟢         |
| 7   | No Space Left On Device 🗂️ |  [{Aoc}](https://adventofcode.com/2022/day/7)  [[Code]](https://github.com/Gers2017/aoc-2022-kotlin/blob/main/src/Day07.kt) | 🟣         |
| 8   |    Treetop Tree House 🌲    |  [{Aoc}](https://adventofcode.com/2022/day/8)  [[Code]](https://github.com/Gers2017/aoc-2022-kotlin/blob/main/src/Day08.kt) | 🟢         |
| 9   |       Rope Bridge 🪢        |  [{Aoc}](https://adventofcode.com/2022/day/9)  [[Code]](https://github.com/Gers2017/aoc-2022-kotlin/blob/main/src/Day09.kt) | 🟣         |
| 10  |     Cathode-Ray Tube 👾     | [{Aoc}](https://adventofcode.com/2022/day/10)  [[Code]](https://github.com/Gers2017/aoc-2022-kotlin/blob/main/src/Day10.kt) | 🟡         |
| 11  |   Monkey in the Middle 🐒   | [{Aoc}](https://adventofcode.com/2022/day/11)  [[Code]](https://github.com/Gers2017/aoc-2022-kotlin/blob/main/src/Day11.kt) | 🟣         |
| 12  | Hill Climbing Algorithm 🐑  | [{Aoc}](https://adventofcode.com/2022/day/12)  [[Code]](https://github.com/Gers2017/aoc-2022-kotlin/blob/main/src/Day12.kt) | 🟡         |
| 13  |     Distress Signal ☠️      | [{Aoc}](https://adventofcode.com/2022/day/13)  [[Code]](https://github.com/Gers2017/aoc-2022-kotlin/blob/main/src/Day13.kt) | 🟣         |
| 14  |    Regolith Reservoir ⌛     | [{Aoc}](https://adventofcode.com/2022/day/14)  [[Code]](https://github.com/Gers2017/aoc-2022-kotlin/blob/main/src/Day14.kt) | 🟡         |

### Input files

Input files should be inside `src/resources` as `.txt` files

### Spoilers ahead

```
 ~ ::<>
            / \  ::<>
           /   \
     ~    /     \    ~ ::<>
         |       |  
          \_____/
   ::<>   |.   .|
          \_____/
    ~     / | | \    ~~ ::<>
         /  | |  \
```

## Notes

### Day05

For me this day was half parsing half executing instructions.
The key for this day is to use a `Stack-like` data structure to execute every instruction.
Every column (stack) contains various crates; every column is identified with a number. To emulate this use either a
hashmap or an array.

```
[0] -> [Z, N]
[1] -> [M, C, D]
[2] -> [P]
```

Next are the instructions; to adjust for the 0 index map/array we subtract one from both `X` and `Y`.
Then call `pop` on the `map[X]` `N` times and move the resulting items to `map[Y]`

```
move N from map[X] to map[Y] 
```

### Day07

The task is to find the optimal Directory to delete to be able to update.
To do that you first need to recreate the File system by following the recorded commands.

The key is to use Recursion, using a `Node-like` data structure to emulate directories and files.
Directories can contain other directories and files while files can't

```
Dir  -> [Dir, File, Dir, File, File]
File -> []
```

Both directories and files have an interface in common (name and size), but the way each of them get the size differs.

```
interface Entry {
    name: String
    getSize(): UInt
}

type Dir {
    name: String
    parent?: Dir
    entries: List<Entry>
    getSize(): UInt
}

type File {
    name: String
    getSize(): UInt
}
```

### Day09

Turns out that rope simulations can be fun after all.

Things to consider:

- Only the head knot moves directly by the motion
- The tail is the one with `next` initialized with `null`
- A knot is connected to the next one if the next knot is inside a 9x9 square with the current as the center
- If a knot is not connected we move it to the `closest point` to the next knot
- Part2 is just Part1 but with more knots

Procedure:

- Apply the motion to the head knot.
- Update the rest of the knots
    - If a knot not is not connected then move it to the `closest point` to the next knot else return
    - If the current knot is the tail, then add it's new position to the `tailPositions` set
    - If there's a next knot, update it as well

Finally, the data structures that I chose to use for this day:

```
type Rope {
    head: Knot
    tailPositions: Set<Vector2d>
}

type Knot {
    position: Vector2d
    next?: Knot
}

type Motion {
    direction: Vector2d
    amount: Int
}
```