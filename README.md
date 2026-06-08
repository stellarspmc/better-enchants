# Enchant Outline ( for 1.21.1 NeoForge )

> Unofficial NeoForge port for 1.21.1 because I wanted it

## Special Thanks

- [Da0neDatGotAway](https://github.com/Da0neDatGotAway) for the original 1.21.4+ fabric version

## About

I'm not writing one, go read it yourself in the original mod [here](https://github.com/Da0neDatGotAway/better-enchants)

## Progress Map

- [x] 1.21.1 NeoForge
- [x] Item rendering
- [x] Block Entity (Shield / Trident) rendering
  - [x] Thrown Trident rendering
  - [ ] Arrow rendering (just an idea)
- [x] Armor rendering
  - [x] Elytra rendering
- [x] Performance Mod Support
- [ ] Content Mod Support (read below)
- [x] Config
  - [x] Screen

## Mod Support

### Performance Mods
- [x] Sodium (v0.8.12)
- [x] Sodium (v0.6.13)
- [ ] Iris (yet to be tested)
- [x] ImmediateFast
- [x] ModernFix
- [x] FerriteCore
- [x] Model Gap Fix
- [x] Particle Core
- [x] Ixeris
- [x] Gnetum

> Safe to assume that most performance mods are supported.

### Content Mods
- [x] EMI
- [x] Awesome 3D Mace
- [x] EvilCraft
- [x] Create
- [x] Epic Knights (except for shields)
- [x] Farmer's Delight
- [x] Advanced Netherite
- [x] Boss of Mass Destruction (probably?)
- [x] Punchy (safe to assume applies to Hold My Items and other similar mods)
- [x] Not Enough Animations
- [x] Avaritia (the codebase is ruined by this mod)
- [ ] Silent Gear (broken on v3.3.4, branching to use mixins for avaritia)
- [x] Sable / Aeronautics (at least it doesn't crash)
- [x] Reliquary (mostly working, probably requires some tinkering tho)
- [ ] Sinytra Connector (ytb tested)

## Won't Fix
- [ ] TACZ (no way, might support soon)
- [ ] Cataclysm (partial support)
- [ ] Artifacts (partial support, but come on why do you need to enchant your artifact)
- [ ] Mekanism (considering but not planned, too messy, definitely will ruin codebase)

> Safe to assume most content mods are supported.
> 
> There's of course exceptions, like cataclysm / tacz, there is no way that this simple implementation would support such heavy mod
> 
> Some edge cases are not tested, it would be great for it to be reported via issues tab
> 
> In my own modpack, I have done some simple testing, and the spark profiler shows that there are basically no performance loss