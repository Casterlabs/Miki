const files = {
    get: function (file) {
        return Native.getFile(file);
    },

    read: function (file) {
        return Native.getFile(file);
    },

    exists: function (file) {
        return Native.fileExists(file);
    },

    create: function (file) {
        return Native.createFile(file);
    },

    write: function (file, content) {
        return Native.write(file, content);
    }

};

const internet = {
    get: function (url) {
        return Native.webRequest(url);
    },

    post: function (url, body) {
        return Native.webRequest(url, {
            method: "post",
            body: body
        });
    },

    send: function (url, data) {
        return Native.webRequest(url, data);
    }
};

const require = function(file) {
	Native.require(file);
}