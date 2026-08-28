import 'package:flutter/material.dart';

class LanguagesSection extends StatelessWidget {
  final List<String?> languages;

  const LanguagesSection({super.key, required this.languages});

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Languages', style: Theme.of(context).textTheme.titleMedium),
            const SizedBox(height: 8),
            if (languages.isEmpty)
              const Text('null')
            else
              Wrap(
                spacing: 8,
                runSpacing: 8,
                children: languages
                    .map(
                      (language) => Chip(
                        label: Text(language ?? 'null'),
                      ),
                    )
                    .toList(),
              ),
          ],
        ),
      ),
    );
  }
}
