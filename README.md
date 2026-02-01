# Killffa

Killffa is a fast-paced free-for-all arena plugin for Paper 1.21.x. Players can join via command, GUI, or NPC, fight with a starter kit, fall into the void without losing their inventory, and keep track of their stats and kill streaks.

## Features
- FFA arena join/leave with a configurable spawn.
- GUI menu for joining, viewing stats, and browsing the top kills list.
- Citizens-powered join NPC (optional).
- PlaceholderAPI placeholders for stats and streaks (optional, works with ajLeaderboards).
- Kill streak tracking with configurable broadcast milestones and rewards.
- Spawn protection on join/respawn to prevent immediate spawn kills.
- Automatic healing and food restoration on kills.
- Temporary sand blocks for arena builders (auto-remove after ~5 seconds).

## Commands
| Command | Description | Permission |
| --- | --- | --- |
| `/killffa info` | Plugin info | none |
| `/killffa join` | Join the arena | `killffa.join` |
| `/killffa leave` | Leave the arena | `killffa.join` |
| `/killffa menu` | Open the GUI | `killffa.menu` |
| `/killffa stats [player]` | View stats | `killffa.stats` |
| `/killffa setspawn` | Set arena spawn | `killffa.admin` |
| `/killffa slay <player>` | Eliminate a player | `killffa.admin` |
| `/killffa setmax <number>` | Set max players | `killffa.admin` |
| `/killffa resetstats <player>` | Reset stats | `killffa.admin` |
| `/killffa reload` | Reload config | `killffa.admin` |
| `/killffa setjoinnpc` | Spawn join NPC | `killffa.admin` |
| `/killffa removejoinnpc` | Remove join NPC | `killffa.admin` |

## Placeholders (PlaceholderAPI)
These can be used in ajLeaderboards or any PlaceholderAPI-compatible plugin:
- `%killffa_kills%`
- `%killffa_deaths%`
- `%killffa_kdr%`
- `%killffa_streak%`
- `%killffa_best_streak%`

## Configuration
```yaml
max-players: 8
join-npc-id: -1
spawn-protection-seconds: 3
kill-heal-hearts: 2.0
streak-announcements:
  - 3
  - 5
  - 10
streak-reward-item: GOLDEN_APPLE
streak-reward-amount: 1
spawn:
  world: world
  x: 0.0
  y: 100.0
  z: 0.0
  yaw: 0.0
  pitch: 0.0
```

## Building
```bash
mvn clean package
```

## Requirements
- Java 21
- Paper 1.21.x
- Optional: Citizens, PlaceholderAPI, ajLeaderboards
