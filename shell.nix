with import <nixpkgs> {};

stdenv.mkDerivation {
  name = "blockfrost-scala";
  buildInputs = [
    sbt
    jdk11
  ];
}
