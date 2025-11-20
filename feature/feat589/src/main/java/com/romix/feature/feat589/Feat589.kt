package com.romix.feature.feat589

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat589Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat589UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat589FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat589UserSummary
)

data class Feat589UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat589NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat589Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat589Config = Feat589Config()
) {

    fun loadSnapshot(userId: Long): Feat589NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat589NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat589UserSummary {
        return Feat589UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat589FeedItem> {
        val result = java.util.ArrayList<Feat589FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat589FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat589UiMapper {

    fun mapToUi(model: List<Feat589FeedItem>): Feat589UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat589UiModel(
            header = UiText("Feat589 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat589UiModel =
        Feat589UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat589UiModel =
        Feat589UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat589UiModel =
        Feat589UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat589Service(
    private val repository: Feat589Repository,
    private val uiMapper: Feat589UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat589UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat589UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat589UserItem1(val user: CoreUser, val label: String)
data class Feat589UserItem2(val user: CoreUser, val label: String)
data class Feat589UserItem3(val user: CoreUser, val label: String)
data class Feat589UserItem4(val user: CoreUser, val label: String)
data class Feat589UserItem5(val user: CoreUser, val label: String)
data class Feat589UserItem6(val user: CoreUser, val label: String)
data class Feat589UserItem7(val user: CoreUser, val label: String)
data class Feat589UserItem8(val user: CoreUser, val label: String)
data class Feat589UserItem9(val user: CoreUser, val label: String)
data class Feat589UserItem10(val user: CoreUser, val label: String)

data class Feat589StateBlock1(val state: Feat589UiModel, val checksum: Int)
data class Feat589StateBlock2(val state: Feat589UiModel, val checksum: Int)
data class Feat589StateBlock3(val state: Feat589UiModel, val checksum: Int)
data class Feat589StateBlock4(val state: Feat589UiModel, val checksum: Int)
data class Feat589StateBlock5(val state: Feat589UiModel, val checksum: Int)
data class Feat589StateBlock6(val state: Feat589UiModel, val checksum: Int)
data class Feat589StateBlock7(val state: Feat589UiModel, val checksum: Int)
data class Feat589StateBlock8(val state: Feat589UiModel, val checksum: Int)
data class Feat589StateBlock9(val state: Feat589UiModel, val checksum: Int)
data class Feat589StateBlock10(val state: Feat589UiModel, val checksum: Int)

fun buildFeat589UserItem(user: CoreUser, index: Int): Feat589UserItem1 {
    return Feat589UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat589StateBlock(model: Feat589UiModel): Feat589StateBlock1 {
    return Feat589StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat589UserSummary> {
    val list = java.util.ArrayList<Feat589UserSummary>(users.size)
    for (user in users) {
        list += Feat589UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat589UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat589UiModel {
    val summaries = (0 until count).map {
        Feat589UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat589UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat589UiModel> {
    val models = java.util.ArrayList<Feat589UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat589AnalyticsEvent1(val name: String, val value: String)
data class Feat589AnalyticsEvent2(val name: String, val value: String)
data class Feat589AnalyticsEvent3(val name: String, val value: String)
data class Feat589AnalyticsEvent4(val name: String, val value: String)
data class Feat589AnalyticsEvent5(val name: String, val value: String)
data class Feat589AnalyticsEvent6(val name: String, val value: String)
data class Feat589AnalyticsEvent7(val name: String, val value: String)
data class Feat589AnalyticsEvent8(val name: String, val value: String)
data class Feat589AnalyticsEvent9(val name: String, val value: String)
data class Feat589AnalyticsEvent10(val name: String, val value: String)

fun logFeat589Event1(event: Feat589AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat589Event2(event: Feat589AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat589Event3(event: Feat589AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat589Event4(event: Feat589AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat589Event5(event: Feat589AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat589Event6(event: Feat589AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat589Event7(event: Feat589AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat589Event8(event: Feat589AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat589Event9(event: Feat589AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat589Event10(event: Feat589AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat589Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat589Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat589Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat589Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat589Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat589Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat589Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat589Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat589Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat589Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat589(u: CoreUser): Feat589Projection1 =
    Feat589Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat589Projection1> {
    val list = java.util.ArrayList<Feat589Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat589(u)
    }
    return list
}
