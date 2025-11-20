package com.romix.feature.feat85

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat85Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat85UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat85FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat85UserSummary
)

data class Feat85UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat85NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat85Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat85Config = Feat85Config()
) {

    fun loadSnapshot(userId: Long): Feat85NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat85NetworkSnapshot(
            users = listOf(user),
            posts = posts,
            rawHash = hash
        )
    }

    private fun snapshotChecksum(user: ApiUserDto, posts: List<ApiPostDto>): Int {
        var result = 1
        result = 31 * result + user.id.hashCode()
        result = 31 * result + user.name.hashCode()
        for (post in posts) {
            result = 31 * result + post.id.hashCode()
            result = 31 * result + post.title.hashCode()
        }
        return result
    }

    fun toUserSummary(coreUser: CoreUser): Feat85UserSummary {
        return Feat85UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat85FeedItem> {
        val result = java.util.ArrayList<Feat85FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat85FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat85UiMapper {

    fun mapToUi(model: List<Feat85FeedItem>): Feat85UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat85UiModel(
            header = UiText("Feat85 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat85UiModel =
        Feat85UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat85UiModel =
        Feat85UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat85UiModel =
        Feat85UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat85Service(
    private val repository: Feat85Repository,
    private val uiMapper: Feat85UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat85UiModel {
        val snapshot = repository.loadSnapshot(userId)
        if (snapshot.users.isEmpty()) {
            return uiMapper.emptyState()
        }
        val coreUser = CoreUser(
            id = snapshot.users[0].id,
            name = snapshot.users[0].name,
            email = null,
            isActive = true
        )
        val items = repository.toFeedItems(listOf(coreUser))
        return uiMapper.mapToUi(items)
    }

    fun ping(path: String): Int {
        val request = NetworkRequest(
            path = path,
            method = "GET",
            body = null
        )
        val response = networkClient.execute(request)
        return response.code
    }

    fun demoComplexFlow(usersCount: Int): Feat85UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat85UserItem1(val user: CoreUser, val label: String)
data class Feat85UserItem2(val user: CoreUser, val label: String)
data class Feat85UserItem3(val user: CoreUser, val label: String)
data class Feat85UserItem4(val user: CoreUser, val label: String)
data class Feat85UserItem5(val user: CoreUser, val label: String)
data class Feat85UserItem6(val user: CoreUser, val label: String)
data class Feat85UserItem7(val user: CoreUser, val label: String)
data class Feat85UserItem8(val user: CoreUser, val label: String)
data class Feat85UserItem9(val user: CoreUser, val label: String)
data class Feat85UserItem10(val user: CoreUser, val label: String)

data class Feat85StateBlock1(val state: Feat85UiModel, val checksum: Int)
data class Feat85StateBlock2(val state: Feat85UiModel, val checksum: Int)
data class Feat85StateBlock3(val state: Feat85UiModel, val checksum: Int)
data class Feat85StateBlock4(val state: Feat85UiModel, val checksum: Int)
data class Feat85StateBlock5(val state: Feat85UiModel, val checksum: Int)
data class Feat85StateBlock6(val state: Feat85UiModel, val checksum: Int)
data class Feat85StateBlock7(val state: Feat85UiModel, val checksum: Int)
data class Feat85StateBlock8(val state: Feat85UiModel, val checksum: Int)
data class Feat85StateBlock9(val state: Feat85UiModel, val checksum: Int)
data class Feat85StateBlock10(val state: Feat85UiModel, val checksum: Int)

fun buildFeat85UserItem(user: CoreUser, index: Int): Feat85UserItem1 {
    return Feat85UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat85StateBlock(model: Feat85UiModel): Feat85StateBlock1 {
    return Feat85StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat85UserSummary> {
    val list = java.util.ArrayList<Feat85UserSummary>(users.size)
    for (user in users) {
        list += Feat85UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat85UserSummary>): List<UiListItem> {
    val items = java.util.ArrayList<UiListItem>(summaries.size)
    for ((index, summary) in summaries.withIndex()) {
        items += UiListItem(
            id = index.toLong(),
            title = summary.name,
            subtitle = if (summary.isActive) "Active" else "Inactive",
            selected = summary.isActive
        )
    }
    return items
}

fun createLargeUiModel(count: Int): Feat85UiModel {
    val summaries = (0 until count).map {
        Feat85UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat85UiModel(
        header = UiText("Large model $count"),
        items = items,
        loading = false,
        error = null
    )
}

fun buildSequentialUsers(count: Int): List<CoreUser> {
    val list = java.util.ArrayList<CoreUser>(count)
    for (i in 0 until count) {
        list += CoreUser(
            id = i.toLong(),
            name = "User-$i",
            email = null,
            isActive = i % 3 != 0
        )
    }
    return list
}

fun mapToUiTextList(users: List<CoreUser>): List<UiText> {
    val list = java.util.ArrayList<UiText>(users.size)
    for (user in users) {
        list += UiText("User: ${user.name}")
    }
    return list
}

fun buildManyUiModels(repeat: Int): List<Feat85UiModel> {
    val models = java.util.ArrayList<Feat85UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat85AnalyticsEvent1(val name: String, val value: String)
data class Feat85AnalyticsEvent2(val name: String, val value: String)
data class Feat85AnalyticsEvent3(val name: String, val value: String)
data class Feat85AnalyticsEvent4(val name: String, val value: String)
data class Feat85AnalyticsEvent5(val name: String, val value: String)
data class Feat85AnalyticsEvent6(val name: String, val value: String)
data class Feat85AnalyticsEvent7(val name: String, val value: String)
data class Feat85AnalyticsEvent8(val name: String, val value: String)
data class Feat85AnalyticsEvent9(val name: String, val value: String)
data class Feat85AnalyticsEvent10(val name: String, val value: String)

fun logFeat85Event1(event: Feat85AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat85Event2(event: Feat85AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat85Event3(event: Feat85AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat85Event4(event: Feat85AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat85Event5(event: Feat85AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat85Event6(event: Feat85AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat85Event7(event: Feat85AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat85Event8(event: Feat85AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat85Event9(event: Feat85AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat85Event10(event: Feat85AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat85Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat85Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat85Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat85Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat85Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat85Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat85Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat85Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat85Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat85Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat85(u: CoreUser): Feat85Projection1 =
    Feat85Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat85Projection1> {
    val list = java.util.ArrayList<Feat85Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat85(u)
    }
    return list
}
