# jmx-spec — Make JMeter .jmx Files Human-Readable for Code Review

JMeter `.jmx` files are hard to review.

Raw XML diffs are noisy and make it difficult to understand what actually changed.

**jmx-spec converts `.jmx` files into human-readable specifications optimized for code review.**

---

## ✨ Features

* Convert `.jmx` → readable test specification
* Works seamlessly with Git diff / Pull Request review
* Simple CLI usage
* Outputs clean, structured, developer-friendly format

---

## ✅ Current Support

jmx-spec currently focuses on commonly used and high-impact components in JMeter test plans:

### Supported Elements

* **Test Plan**
* **User Defined Variables (under Test Plan)**
* **Thread Group**
* **Constant Timer**
* **HTTP Header Manager**
* **HTTP Request**

---

### Supported Fields (Examples)

* Users, ramp-up time, duration
* Loop settings (including infinite loop)
* Think time (Constant Timer)
* HTTP method / path / domain / protocol / port
* Headers (converted into readable key-value format)
* Redirect / keep-alive settings
* Error handling behavior

---

⚠️ Not all JMX elements are fully supported yet.
Some fields may still appear in raw or partially formatted form.

---

## 🔥 Example

### Before (JMeter XML)

```xml
<stringProp name="ThreadGroup.num_threads">50</stringProp>
<stringProp name="HTTPSampler.method">POST</stringProp>
```

---

### After (jmx-spec)

```text
users: 50
POST /api/login
```

👉 Easy to understand at a glance

---

## 🚀 Usage

```bash
java -jar jmx-spec.jar test.jmx
```

or

```bash
jmx-spec test.jmx
```

---

## 🔍 Git Workflow

```bash
jmx-spec test.jmx > spec.md
git add spec.md
```

Now you can review changes directly in Pull Requests.

---

## 💡 Concept

jmx-spec is **not a diff tool**.

👉 It transforms JMX into a format that is *easy to diff* using existing tools like Git and GitHub.

---

## 🛠 Roadmap

* [x] JMX → readable spec conversion
* [x] Support for core JMeter components
* [ ] Extended element support
* [ ] JSON output
* [ ] Structural diff feature
* [ ] CI/CD integration

---

## 💡 Design Philosophy

jmx-spec takes an incremental approach.

It prioritizes the most commonly used JMeter elements first, then expands support based on real-world usage and feedback.

If you need support for additional elements, feel free to open an issue.

---

## 🤝 Contribution

Issues and Pull Requests are welcome!

---

## 📄 License

MIT
