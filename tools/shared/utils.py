def strip_prefix_suffix(prefix, suffix):
    def stripper(s):
        if s.startswith(prefix):
            s = s[len(prefix):]
        if s.endswith(suffix):
            s = s[:-len(suffix)]
        return s
    return stripper