resolvers += Resolver.url("hmrc-sbt-plugin-releases", url("https://dl.bintray.com/hmrc/sbt-plugin-releases"))(Resolver.ivyStylePatterns)
resolvers += "HMRC Releases" at "https://dl.bintray.com/hmrc/releases"

addSbtPlugin("uk.gov.hmrc" % "sbt-git-versioning" % "2.1.0")
addSbtPlugin("uk.gov.hmrc" % "sbt-artifactory" % "1.0.0")

addSbtPlugin("org.xerial.sbt" % "sbt-pack" % "0.13")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.6.1")
addSbtPlugin("org.scalastyle"    %% "scalastyle-sbt-plugin" % "1.0.0")
