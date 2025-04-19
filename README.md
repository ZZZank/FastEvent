
# ModTemplatez

An Architectury Loom based template workspace for Minecraft Forge mod development.

This mod template is initially configured to provide support for 1.16 Forge mod developing, but you can easily change supported version by modifying `gradle.properties`

## Features

- features from [Architectury Templates](https://github.com/architectury/architectury-templates), the base of this template.
- variable autofilling for `mods.toml` and `pack.mcmeta`, preventing the hassle of locating every related names and renaming them
- easy to use mapping selection: official, parchment, or yarn
- preconfigured [Lombok](https://projectlombok.org/) and [JvmDowngrader](https://github.com/unimined/JvmDowngrader) that can be enabled/disabled easily
- `shade` for Jar shadowing
- preconfigured publishing tasks for publishing to Maven/CurseForge/Modrinth
- mixin config auto generation
- optional, pre-configured access widener
- preconfigured dependency management, you can add dependencies in `./gradle/scripts/dependencies.gradle`
- easy local library setup, by adding Jar files to `gradle/local_libs`
- changelog generation

## Changing target MC version

If you want to change target version from 1.16 to some other version, you need to change these values in `gradle.properties`:
- `minecraft_version`
- choose corresponding `forge_version` and `loader_version_range`
- `parchment_version` or `yarn_version`, if you're not using "official" mapping
- possibly `resource_pack_format` to make MC less annoyed about mismatched resource pack format
- `target_java_version`, use `16` for 1.17, `17` for 1.18+
