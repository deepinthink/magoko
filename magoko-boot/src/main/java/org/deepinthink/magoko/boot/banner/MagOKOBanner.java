/*
 * Copyright (c) 2021-present deepinthink. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.deepinthink.magoko.boot.banner;

import java.io.PrintStream;
import java.util.*;
import org.springframework.boot.Banner;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

public final class MagOKOBanner implements Banner {

  private static final String[] DEFAULT_BANNER = {
    "███╗   ███╗ █████╗  ██████╗  ██████╗ ██╗  ██╗ ██████╗",
    "████╗ ████║██╔══██╗██╔════╝ ██╔═══██╗██║ ██╔╝██╔═══██╗",
    "██╔████╔██║███████║██║  ███╗██║   ██║█████╔╝ ██║   ██║",
    "██║╚██╔╝██║██╔══██║██║   ██║██║   ██║██╔═██╗ ██║   ██║",
    "██║ ╚═╝ ██║██║  ██║╚██████╔╝╚██████╔╝██║  ██╗╚██████╔╝",
    "╚═╝     ╚═╝╚═╝  ╚═╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝ ╚═════╝ ",
  };

  private static final String DEFAULT_COPYRIGHT =
      "© 2021-present deepinthink. All rights reserved.";

  private final String[] banner;
  private final List<String> tags;
  private final String copyright;

  MagOKOBanner(Builder builder) {
    this.banner = builder.banner;
    this.tags = Collections.unmodifiableList(builder.tags);
    this.copyright = builder.copyright;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  @Override
  public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
    Arrays.stream(this.banner).forEach(out::println);
    this.tags.forEach(out::println);
    out.println(this.copyright);
  }

  public static class Builder {
    String[] banner = DEFAULT_BANNER;
    List<String> tags = new ArrayList<>();
    String copyright = DEFAULT_COPYRIGHT;

    public Builder banner(String[] banner) {
      Optional.ofNullable(banner).ifPresent(b -> this.banner = b);
      return this;
    }

    public Builder tag(String tag) {
      Optional.ofNullable(tag).filter(StringUtils::hasText).ifPresent(this.tags::add);
      return this;
    }

    public Builder copyright(String copyright) {
      Optional.ofNullable(copyright)
          .filter(StringUtils::hasText)
          .ifPresent(c -> this.copyright = c);
      return this;
    }

    public MagOKOBanner build() {
      return new MagOKOBanner(this);
    }
  }
}
