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
        return Native.webRequest(url, null, null);
    },

    post: function (url, body) {
        return Native.webRequest(url, "post", body);
    },

    method: function (url, method, body) {
        return Native.webRequest(url, method, body);
    }
};