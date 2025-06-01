# Activerse

**Activerse** is a **Java Game/Simulation/Physics Engine** designed to simplify the game-making experience for 2D games. With Activerse, you have access to basic yet powerful methods that allow you to create your game from the ground up. The possibilities are endless—design and develop games that match your vision.

## 🚀 What is Activerse?

Activerse is built to empower developers of all skill levels to create 2D games and simulations. From basic physics simulations to fully interactive games, Activerse provides the foundation, and it's up to you to build the rest. The engine is regularly updated with new features and improvements, ensuring you have the tools needed to bring your ideas to life.

## 🔧 Installation

To start using Activerse, follow these steps:

1. **Download the Latest Version**: Visit the [Activerse GitHub Releases](https://github.com/Knivier/Activerse/releases) page and download the source code files from the latest version or the version that best suits your needs.

2. **Set Up Your Project**:
   - Extract the downloaded files to your desired project directory.
   - Open the project in your favorite Java IDE (such as IntelliJ IDEA, Eclipse, or VS Code).

3. **Build and Run**:
   - Compile the source code within your IDE.
   - Run the engine by executing `Main.java`.
   - Ensure all debugging modes are turned **off** (this occurs by default).

4. **Start Building Your Game**:
   - Use the provided classes and methods in Activerse to begin developing your game.
   - Customize and expand upon the engine to fit your project requirements.

## 📚 Getting Started

Reading this file is just the beginning! For more detailed guidance, examples, and documentation, explore the following resources:

- **[Activerse GitHub Wiki](https://knivier.com/ActiWiki/wiki.html)**: Contains comprehensive tutorials, code examples, and a deep dive into all engine functionalities.
- **[Official Activerse Website](https://knivier.github.io/KnivierWeb/activerseinfo.html)**: Visit the website for additional information, updates, and community support.
- **[v1.1.2 Profile Research](https://github.com/user-attachments/files/16165118/Research.Notes.for.v1.1.2.Optimizations.pdf)**: Contains the profiling and research information of v1.1.2 with provided explanations and evidence.
- **Game Examples**: As of v1.3.3, examples of games created in Activerse are now stored in the "Examples" folder. These have their own engine components, so it's best to CD into the project and start up a directory in that for the least confusion. The Activerse Exemplers GitHub has been removed.

> **⚠️ WARNING**  
> Always use the latest version of Activerse to prevent bugs and avoid compatibility issues. Running outdated versions may result in unexpected behavior or crashes.
> **💡 IMPORTANT**  
> When starting the engine, always run `Main.java`.

## Error handling

As of v1.3.1, Activerse uses the **ACESH System** or "Activerse Concurrent Error Handling System". This is a simple and native error reporting interface that utilizes Java's own stack trace along with error codes; should you encounter broken code (or a developer extending the engine), they will know where the error occurred in greater detail.

This system is always improving. Activerse developers are not responsible for broken or malicious code within their product.

File name extensions:

- 1A - Activerse.java
- 2A - ActiverseImage.java
- 3A - ActiverseMouseInfo.java
- 4A - ActiverseSound.ava
- 5A - Actor.Java
- 6A - CollisionnManager.java
- 7A - GameLoop.Java
- 8A - KeyBoardInfo.java
- 9A - MemoryTracker.Java
- 10A - World.Java
- 11A - Item.java
- 1B - ActorVector.java
- 2B - FileUtils.java
- 3B - PerlinNoise.java
- 4B - Physics.java
- 5B - Timer.java
- 6B - WorldGeneration.java

- PROP.IN.OUT.IO - Activerse.properties error; major screwup by developers

Keywords that may be helpful

- IO - A centeralized breakdown; this is typically a result of multiple errors that are being caught by ACESH that cause an IO error
- IN - An input breakdown
- OUT - An output breakdown
- LN - Error line reporting
- DEV - Contact Dev's; this is not a good error
- CONNTO - A connecting line of code that connects class to class that may cause errors in each

ACEHS can continue running your game even with errors. However, there are certain fatal errors that will be crashed. If you don't care about possible headaches, you can find the ACEHS line and convert the catch expression to instead continue execution.

Happy coding, and enjoy bringing your creations to life with Activerse!

## 📝 License

**Activerse © 2025 by Knivier / Agniva** is licensed under **[CC BY-NC-SA 4.0](https://creativecommons.org/licenses/by-nc-sa/4.0/)**.
Well, what does this mean for you?

- **Attribution**: You must give appropriate credit, provide a link to the license, and indicate if changes were made. You may do so in any reasonable manner, but not in any way that suggests the licensor endorses you or your use.
- **NonCommercial**: You may not use the material for commercial purposes.
- **ShareAlike**: If you remix, transform, or build upon the material, you must distribute your contributions under the same license as the original.
- **No additional restrictions**: You may not apply legal terms or technological measures that legally restrict others from doing anything the license permits.
- **Disclaimer of Warranties and Limitation of Liability**: The material is provided "as-is" without warranties of any kind, and the licensor is not liable for any damages arising from its use.

---
