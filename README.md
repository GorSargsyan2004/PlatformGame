# 🎮 Platform Game 

## A 2D game where the task is to survive as long as possible and get scores. The best score will be recorded.

---

## The goals of the game are
- Killing attacking monsters
- Preventing the health from becoming zero
- Getting aids to restore health
- Resisting the attacks as much as possible

```
PlatformGame
├── README.md
├── pom.xml
└── src
    └── main
          ├── java
          │    ├── animations
          │    │    └── Animation.java
          │    ├── entities
          │    │    ├── Aid.java
          │    │    ├── Enemy.java
          │    │    ├── Entity.java
          │    │    └── Player.java
          │    ├── score_recorder
          │    │    └── ScoreRecorder.java
          │    ├── inputs
          │    │    ├── KeyboardInputs.java
          │    │    └── MouseInputs.java
          │    └── main
          │        ├── Game.java    
          │        ├── GameAlgorithm.java    
          │        ├── GamePanel.java
          │        ├── GameWindow.java
          │        └── MainClass.java
          └── resources
```

![Warrior_Idle_1.png](src/main/resources/Player/Idle/Warrior_Idle_1.png) The Player

---
### Monsters
![Idle.png](src/main/resources/Enemy/Goblin/Idle.png)
![Idle.png](src/main/resources/Enemy/Mushroom/Idle.png)
![Idle.png](src/main/resources/Enemy/Skeleton/Idle.png)
![Flight.png](src/main/resources/Enemy/Flying%20eye/Flight.png)