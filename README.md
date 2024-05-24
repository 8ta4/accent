# accent

## Accentuate the Negative

> What is `accent`?

`accent` is a tool to identify mispronunciations.

## Setup

> How do you set up `accent`?

1. Make sure you're using a Mac.

1. Install [devenv](https://github.com/cachix/devenv/blob/2837f4989338aaf03b5b4cf8bad91fe27150d984/docs/getting-started.md#installation).

1. Install [direnv](https://github.com/cachix/devenv/blob/2837f4989338aaf03b5b4cf8bad91fe27150d984/docs/automatic-shell-activation.md#installing-direnv).

1. Open a terminal window.

1. Run the following commands:

   ```sh
   git clone git@github.com:8ta4/accent.git
   cd accent
   direnv allow
   ```

1. Open the `config.yaml` file located in the `accent` directory.

1. Copy a Deepgram API key from [the Deepgram website](https://deepgram.com/).

1. Add the following field to the `config.yaml` file, replacing `your_deepgram_api_key` with your actual Deepgram API key:

    ```yaml
    deepgram: your_deepgram_api_key
    ```

1. Copy an OpenAI API key from [the OpenAI website](https://platform.openai.com/api-keys).

1. Add the following field to the `config.yaml` file, replacing `your_openai_api_key` with your actual OpenAI API key:

    ```yaml
    openai: your_openai_api_key
    ```

1. Save the `config.yaml` file.

## Usage

> How do I check how my pronunciation's doing?

1. Open a terminal on your computer.

1. Navigate to the directory where `accent` is installed.

1. Run the command `accent` to start the application.

1. Start speaking.

1. Press **Space** when you want to evaluate your pronunciation up to that moment.

> How do I evaluate my pronunciation the second time?

After your initial evaluation, simply continue speaking and press **Space** whenever you want feedback on the new segment of speech you've just spoken. Each press of **Space** evaluates only the speech from the end of the last evaluation to the moment you press the key again.
