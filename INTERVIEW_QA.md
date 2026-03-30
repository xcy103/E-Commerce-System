# Interview Questions and Answers

## Question 1: What is the difference between `==` and `===` in JavaScript?

### Answer:
`==` checks for equality of value, while `===` checks for equality of both value and type. For example:

```javascript
console.log(5 == '5'); // true
console.log(5 === '5'); // false
```

### Explanation:
Using `===` is generally better practice as it avoids type coercion issues.

---

## Question 2: What are closures in JavaScript?

### Answer:
A closure is a function that retains access to its lexical scope, even when the function is executed outside that scope.

### Example:
```javascript
function outerFunction() {
    let outerVariable = 'I am outside!';
    function innerFunction() {
        console.log(outerVariable);
    }
    return innerFunction;
}

const innerFunc = outerFunction();
innerFunc(); // logs 'I am outside!'
```

### Diagram:
![Closure Diagram](https://example.com/closure-diagram.png)

---

## Question 3: Explain the concept of Promises in JavaScript.

### Answer:
A Promise is an object representing the eventual completion (or failure) of an asynchronous operation and its resulting value.

### Example:
```javascript
let myPromise = new Promise((resolve, reject) => {
    let success = true;
    if (success) {
        resolve('Operation was successful!');
    } else {
        reject('Operation failed!');
    }
});

myPromise.then((message) => {
    console.log(message);
}).catch((error) => {
    console.log(error);
});
```

---

## Question 4: What is the purpose of the `async/await` syntax?

### Answer:
The `async/await` syntax allows you to write asynchronous code that looks like synchronous code, making it easier to read and maintain.

### Example:
```javascript
async function fetchData() {
    try {
        let response = await fetch('https://api.example.com/data');
        let data = await response.json();
        console.log(data);
    } catch (error) {
        console.log(error);
    }
}
```

---

## Question 5: Explain the concept of `this` in JavaScript.

### Answer:
The value of `this` in JavaScript is determined by how a function is called. It can refer to different objects depending on the context.

### Example:
```javascript
const obj = {
    value: 10,
    getValue: function() {
        return this.value;
    }
};

console.log(obj.getValue()); // 10
```

### Note:
Using `bind()`, `call()`, or `apply()` can change the context of `this`.

---

## Question 6: Describe the event loop in JavaScript.

### Answer:
The event loop is a mechanism that allows JavaScript to perform non-blocking I/O operations despite being single-threaded.

### Explanation:
It does this by executing code, collecting and processing events, and executing queued sub-tasks, in a continuous cycle.

---

## Question 7: What are arrow functions, and how do they differ from regular functions?

### Answer:
Arrow functions provide a concise syntax and do not have their own `this`, making them useful in certain contexts.

### Example:
```javascript
const add = (a, b) => a + b;

console.log(add(2, 3)); // 5
```

---

## Question 8: Explain the concept of hoisting in JavaScript.

### Answer:
Hoisting is a JavaScript mechanism where variables and function declarations are moved to the top of their containing scope during the compile phase.

### Example:
```javascript
console.log(myVar); // undefined
var myVar = 10;
```

---

## Question 9: What is the purpose of `Array.prototype.map`?

### Answer:
The `map` method creates a new array populated with the results of calling a provided function on every element in the calling array.

### Example:
```javascript
const numbers = [1, 2, 3];
const doubled = numbers.map(num => num * 2);
console.log(doubled); // [2, 4, 6]
```

---

## Question 10: What is a RESTful API?

### Answer:
A RESTful API is an application programming interface that adheres to the principles of Representational State Transfer (REST).

### Characteristics:
- Stateless: Each request from client to server must contain all the information needed to understand and process the request.
- Client-Server architecture.

---

## Question 11: What are the different types of HTTP methods?

### Answer:
Common HTTP methods include GET, POST, PUT, DELETE, PATCH.

### Examples:
- **GET**: Retrieve data from a server.
- **POST**: Send data to a server.
- **PUT**: Update existing data.
- **DELETE**: Remove data from a server.

---

## Question 12: What is Cross-Origin Resource Sharing (CORS)?

### Answer:
CORS is a security feature implemented by web browsers to prevent malicious sites from reading sensitive data from another site.

### Explanation:
This feature allows or restricts resources on a web page to be requested from another domain outside the domain from which the first resource was served.

---

## Question 13: Explain the difference between `null` and `undefined`.

### Answer:
- `null`: A value that represents the absence of a value.
- `undefined`: A variable that has been declared but has not yet been assigned a value.

### Example:
```javascript
let a = null;
let b;
console.log(a); // null
console.log(b); // undefined
```

---

## Question 14: What is the purpose of `try/catch` in JavaScript?

### Answer:
The `try/catch` statement lets you test a block of code for errors. If an error occurs, it will jump to the `catch` block.

### Example:
```javascript
try {
    let result = riskyFunction();
} catch (error) {
    console.error('Error caught:', error);
}
```

---

## Question 15: What is debouncing in JavaScript?

### Answer:
Debouncing is a programming practice used to ensure that time-consuming tasks do not fire so often, thus improving performance.

### Example:
```javascript
function debounce(func, delay) {
    let timeout;
    return function(...args) {
        clearTimeout(timeout);
        timeout = setTimeout(() => func.apply(this, args), delay);
    };
}
```

---

This document provides a brief overview of interview questions and answers that can help you prepare for interviews effectively. The formatted content is ready for easy reference and can be saved in PDF format by using any markdown to PDF converter.