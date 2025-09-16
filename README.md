# cmp-animatedcounter
A counter widget that animates changes by spinning through intermediate values, using 
[Compose Multiplatform (CMP)](https://www.jetbrains.com/compose-multiplatform/). I implemented this after 
trying to use other number-ticker or rolling-number type widgets and being frustrated with them.  
With this widget, a number increase always animates the digits from above.

This widget looks best when changes to the number are relatively small.


https://github.com/user-attachments/assets/6855a0d8-0b54-4d0a-9588-62544132d1ff


## Integration
This library is deployed to GitHub Packages. Unlike Maven Central, GitHub requires authentication just to fetch. 

### Obtain a classic personal access token
You need a GitHub classic access token with proper scopes, to install this dependency. 
[See the introduction on GitHub Docs](https://docs.github.com/en/packages/learn-github-packages/introduction-to-github-packages#authenticating-to-github-packages)

### Configure the repository
Add the following `maven` block to your `repositories` block, which will be in `settings.gradle.kts`, 
inside `dependencyResolutionManagement`, if your KMP project is using the current project template.
```
maven {
    url = uri("https://maven.pkg.github.com/CapnSpellcheck/cmp-animatedcounter")
    credentials {
        username = System.getenv("GITHUB_USER") ?: settings.extra.properties["GITHUB_USER"] as String
        password = System.getenv("GITHUB_PERSONAL_ACCESS_TOKEN") ?: settings.extra.properties["GITHUB_PERSONAL_ACCESS_TOKEN"] as String
    }
}
```
You need to set these 2 environment variables or create a `gradle.properties` in `~/.gradle/gradle.properties`. Note: you can change the names of the environment variables as you wish.  
Your whole `dependencyResolutionManagement` might look like this:
```
dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/CapnSpellcheck/cmp-animatedcounter")
            credentials {
                username = System.getenv("GITHUB_USER") ?: settings.extra.properties["GITHUB_USER"] as String
                password = System.getenv("GITHUB_PERSONAL_ACCESS_TOKEN") ?: settings.extra.properties["GITHUB_PERSONAL_ACCESS_TOKEN"] as String
            }
        }
    }
}
```

### Add cmp-animatedcounter
Current version is 1.0.1. You may add something like this to `libs.versions.toml`, don't forget to add the
reference to `commonMain` dependencies.
```
animated-counter = { module = "com.letstwinkle:cmp-animatedcounter", version = "1.0.1" }
... build.gradle.kts ...
    implementation(libs.animated.counter)
```

## Usage
Place `AnimatedCounter` in your composable:
```
AnimatedCounter(
   value = 100,
   modifier = Modifier,
   animationDelayMsec = 500,
   animationDurationMsec = 500,
   digitSpacing = 1.dp,
   textStyle = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Medium)
)
```
The only required parameter is the `value`. `AnimatedCounter` will animate changes to value, including decreases which are allowed. However, value cannot be negative.

Jump to the source definition to see details of the optional parameters.

## Performance implications
I am not sure of the performance characteristic of extensive clipped drawing in Compose. Possibly the drawing is executed.
If the `value` has large changes, say 100+, it's possible to lose frames on devices with slow CPUs, probably
at the beginning of the animation.

## Missing features / Possible enhancements
While I didn't use any of these ideas myself, I can see how they might be useful, and certainly fit within 
the widget's purpose. While I probably won't flesh these out myself, I will review pull requests. Please
make sure the feature is demoable in the project example.
- Allow negative values: because I didn't implement the negative sign, the API uses `UInt`. This feature would change the 
parameter to `Int`. However, it's not so simple as just adding the sign: I'd expect that increases **always** animate from above -
so the entire drawing offsets would be flipped.
- Option to show thousands groupings: For large values, it could be more readable to show group separators.
- Option to show a small number of decimal places. You'd probably want to add a fixed decimal type, have fun with finding 
a good KMP one.
- Re-entrant animation: If the value is changed while the current value is still animating, it doesn't handle it gracefully. It will
jump to the previous final value and start a new animation to the new final value.
