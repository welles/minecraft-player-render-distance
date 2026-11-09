#!/usr/bin/env bash
# Starts a local server and two clients, Alice and Bob, that join it as soon as it is up, to try the mod in
# multiplayer. Stop everything with Ctrl+C.
#
#   scripts/test-multiplayer.sh [node]        e.g. 1.21.11-fabric (default) or 26.3-neoforge
#   nix-shell --run scripts/test-multiplayer.sh   on NixOS
#
# The server runs in run/test/server, the clients in run/test/Alice and run/test/Bob, each with its own config. The
# server is in offline mode (the clients have no Mojang account in development), creative on a flat world, with a
# view distance of 4 chunks: fly more than 64 blocks away and vanilla would no longer show the other player. Mods for
# a client, e.g. Distant Horizons or a zoom mod, go into run/test/Alice/mods or run/test/Bob/mods. The clients run
# without sound.
set -euo pipefail
cd "$(dirname "$0")/.."

node=${1:-1.21.11-fabric}
if ! grep -qE "match\(\"${node%-*}\".*\"${node##*-}\"" settings.gradle.kts; then
    echo "Unknown node $node, see settings.gradle.kts" >&2
    exit 1
fi

server=run/test/server
mkdir -p "$server"
echo "eula=true" >"$server/eula.txt"
cat >"$server/server.properties" <<EOF
online-mode=false
enforce-secure-profile=false
motd=Player Render Distance test server
gamemode=creative
difficulty=peaceful
level-type=minecraft\:flat
generate-structures=false
spawn-protection=0
allow-flight=true
white-list=false
view-distance=4
simulation-distance=4
EOF

# No sound: OpenAL gets no audio device
export ALSOFT_DRIVERS=null

# One Gradle build runs all three: with the configuration cache, tasks of the same project run in parallel
exec ./gradlew --parallel ":$node:runTestServer" ":$node:runTestClientA" ":$node:runTestClientB"
