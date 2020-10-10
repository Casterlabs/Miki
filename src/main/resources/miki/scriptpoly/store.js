const Store = {
    create: function (filename, defaults) {
        if (!filename) {
            filename = "store.json";
        }

        return function (filename) {
            const name = filename;
            let content = {};

            if (!files.exists(name)) {
                files.create(name);

                if ((defaults !== undefined) && (defaults !== null)) {
                    content = defaults;

                    files.write(name, content);
                }
            } else {
                try {
                    let filecontent = JSON.parse(files.read(name));

                    if (typeof filecontent === "object") {
                        content = filecontent;
                    } else if ((defaults !== undefined) && (defaults !== null)) {
                        content = defaults;

                        files.write(name, content);
                    }
                } catch (e) {
                    console.warn("Could not read store because of an error: " + e);
                    console.warn("Generating default store anyways.");

                    if ((defaults !== undefined) && (defaults !== null)) {
                        content = defaults;

                        files.write(name, content);
                    }
                }
            }

            return {
                get: function (key) {
                    return content[key];
                },

                getOrDefault: function (key, defaultValue) {
                    return (content[key] !== undefined) ? content[key] : defaultValue;
                },

                set: function (key, value) {
                    let old = content[key];

                    content[key] = value;

                    this.save();

                    return old;
                },

                getName: function () {
                    return name;
                },

                has: function (key) {
                    return content[key] !== undefined;
                },

                save: function () {
                    files.write(name, content);
                },

                toString: function () {
                    return JSON.stringify(content);
                }
            }
        }(filename);
    }
};
