# Changelog

All notable changes to this mod are documented in this file. The format is based on
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and the mod follows [Semantic Versioning](https://semver.org/).

## [Unreleased]

### Added

- Other players are visible far beyond the view distance: the server sends them from up to 1024 blocks away, and the
  client draws them even where no chunks are loaded and beyond the entity distance.
- Players beyond half the render distance are drawn closer and smaller, so the fog and Distant Horizons' fade into
  its far terrain do not hide them.
- Settings in `config/playerrenderdistance.properties`, editable in game through NeoForge's mod list or Mod Menu.
