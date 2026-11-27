I'm not waiting a week.

Version 3.0.1:
- Ported to 26.4-pre2
- Marked 26.1-4 jar as compatible with 26.1.1 and 26.1.2 too
- Marked sky angle caching as incompatible with Citadel in versions 1.20.4 and below
  - The sky angle caching in newer versions is compatible, but cannot be backported because it mixins into an interface which doesn't work on older forge
- Fix a bug where specific messages about a config option could be logged even when you manually disabled it
- Fix a bug where some member variables in mixins would not initialize correctly
  - This fixes compat with Darker Depths
- Changed mixin defaultRequire back to 0 to improve compatibility
  - I changed it to 1 in 3.0.0 to catch mistakes but didn't change it back