export default function babelPluginMyPlugin() {
  return {
    visitor: {
      Identifier(path) {
        const name = path.node.name;
        console.log(name)
      },
    },
  };
}
