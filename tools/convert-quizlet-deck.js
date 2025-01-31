// ================================
// Go to the desired quizlet deck page in your browser
// Open the browser console (F12) and paste the following code
// Press Enter to run the code and see the pairs printed in the console
// Copy the output to ../data.json and run the import script
// ================================
function fetchName() {
  const element = document.querySelector("#setPageSetIntroWrapper h1");
  return element.innerText;
}

function fetchFlashcards() {
  let flashcards = [];

  // Get all terms
  const elements = document.querySelectorAll(".SetPageTerms-term .TermText");

  for (let i = 0; i < elements.length; i += 2) {
    if (!elements[i + 1]) {
      console.warn("Odd number of terms, the last term has been ignored");
      break;
    }

    const frontElement = elements[i];
    const backElement = elements[i + 1];

    flashcards.push({
      frontContent: {
        type: "text",
        text: extractContent(frontElement),
        language: { abbrivation: extractAbbrievation(frontElement) }
      },
      backContent: {
        type: "text",
        text: extractContent(backElement),
        language: { abbrivation: extractAbbrievation(backElement) }
      }
    });
  }
  return flashcards;
}

// Ex. extracts content from term element
function extractContent(elem) {
  return elem.innerText;
}

// Extracts language code from term element
// Ex. extracts "fr" from <tag class="lang-fr" />
function extractAbbrievation(elem) {
  return [...elem.classList].find((className) => className.startsWith("lang-")).split("-")[1];
}

const flashcardDeck = {
  name: fetchName(),
  flashcards: fetchFlashcards()
}; 
console.log(JSON.stringify(flashcardDeck, null, 2));