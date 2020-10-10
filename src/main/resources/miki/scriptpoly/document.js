const document = function () {

	return {
		print: function (obj) {
			Native.print(obj);
		},

		println: function (obj) {
			Native.println(obj);
		},

		// voidTypes: ["area", "base", "br", "col", "command", "embed", "hr", "img", "input", "keygen", "link", "meta", "param", "source", "track", "wbr"],

		removeArrayElement: function (element, array) {
			let index = array.indexOf(element);

			if (index == -1) {
				throw "Element is not present in array";
			} else {
				array.splice(index, 1);
			}
		}
	};
}();

// For when we move to the V8 JS interpreter...
/*
class HTMLElement {
	constructor(type) {
		this.type = type.toLowerCase();
		this.children = [];
		this.parent = null;
	}

	appendChild(element) {
		if (element.parent !== null) {

		} else {
			throw "Element is already a child of another element";
		}
	}

	removeChild(element) {
		if (element.parent === this) {
			element.parent = null;
			document.removeArrayElement(element, this.children);
		} else {
			throw "Element is a child of another element";
		}
	}

	remove() {
		if (this.parent === null) {
			throw "Element is not a child of another element"
		} else {
			this.parent.removeChild(this);
		}
	}

	toString() {
		let attributes = "";

		for (const [key, value] of Object.entries(this)) {
			if (!(value instanceof Function)) {
				attributes += " " + key + "\"" + value + "\"";
			}
		}

		if (document.voidTypes.includes(this.type)) {
			return "<" + this.type + attributes + " />";
		} else {

			return "<" + this.type + attributes + ">" + child + "</" + this.type + ">";
		}
	}
};

class HTMLTextElement {
	constructor(text) {
		this.text = text;
		this.parent = null;
	}

	remove() {
		if (this.parent === null) {
			throw "Element is not a child of another element"
		} else {
			this.parent.removeChild(this);
		}
	}

	toString() {
		return this.text;
	}
};
*/