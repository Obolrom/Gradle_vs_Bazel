package com.romix.feature.feat480

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat480Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat480UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat480FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat480UserSummary
)

data class Feat480UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat480NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat480Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat480Config = Feat480Config()
) {

    fun loadSnapshot(userId: Long): Feat480NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat480NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat480UserSummary {
        return Feat480UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat480FeedItem> {
        val result = java.util.ArrayList<Feat480FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat480FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat480UiMapper {

    fun mapToUi(model: List<Feat480FeedItem>): Feat480UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat480UiModel(
            header = UiText("Feat480 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat480UiModel =
        Feat480UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat480UiModel =
        Feat480UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat480UiModel =
        Feat480UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat480Service(
    private val repository: Feat480Repository,
    private val uiMapper: Feat480UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat480UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat480UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat480UserItem1(val user: CoreUser, val label: String)
data class Feat480UserItem2(val user: CoreUser, val label: String)
data class Feat480UserItem3(val user: CoreUser, val label: String)
data class Feat480UserItem4(val user: CoreUser, val label: String)
data class Feat480UserItem5(val user: CoreUser, val label: String)
data class Feat480UserItem6(val user: CoreUser, val label: String)
data class Feat480UserItem7(val user: CoreUser, val label: String)
data class Feat480UserItem8(val user: CoreUser, val label: String)
data class Feat480UserItem9(val user: CoreUser, val label: String)
data class Feat480UserItem10(val user: CoreUser, val label: String)

data class Feat480StateBlock1(val state: Feat480UiModel, val checksum: Int)
data class Feat480StateBlock2(val state: Feat480UiModel, val checksum: Int)
data class Feat480StateBlock3(val state: Feat480UiModel, val checksum: Int)
data class Feat480StateBlock4(val state: Feat480UiModel, val checksum: Int)
data class Feat480StateBlock5(val state: Feat480UiModel, val checksum: Int)
data class Feat480StateBlock6(val state: Feat480UiModel, val checksum: Int)
data class Feat480StateBlock7(val state: Feat480UiModel, val checksum: Int)
data class Feat480StateBlock8(val state: Feat480UiModel, val checksum: Int)
data class Feat480StateBlock9(val state: Feat480UiModel, val checksum: Int)
data class Feat480StateBlock10(val state: Feat480UiModel, val checksum: Int)

fun buildFeat480UserItem(user: CoreUser, index: Int): Feat480UserItem1 {
    return Feat480UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat480StateBlock(model: Feat480UiModel): Feat480StateBlock1 {
    return Feat480StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat480UserSummary> {
    val list = java.util.ArrayList<Feat480UserSummary>(users.size)
    for (user in users) {
        list += Feat480UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat480UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat480UiModel {
    val summaries = (0 until count).map {
        Feat480UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat480UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat480UiModel> {
    val models = java.util.ArrayList<Feat480UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat480AnalyticsEvent1(val name: String, val value: String)
data class Feat480AnalyticsEvent2(val name: String, val value: String)
data class Feat480AnalyticsEvent3(val name: String, val value: String)
data class Feat480AnalyticsEvent4(val name: String, val value: String)
data class Feat480AnalyticsEvent5(val name: String, val value: String)
data class Feat480AnalyticsEvent6(val name: String, val value: String)
data class Feat480AnalyticsEvent7(val name: String, val value: String)
data class Feat480AnalyticsEvent8(val name: String, val value: String)
data class Feat480AnalyticsEvent9(val name: String, val value: String)
data class Feat480AnalyticsEvent10(val name: String, val value: String)

fun logFeat480Event1(event: Feat480AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat480Event2(event: Feat480AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat480Event3(event: Feat480AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat480Event4(event: Feat480AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat480Event5(event: Feat480AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat480Event6(event: Feat480AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat480Event7(event: Feat480AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat480Event8(event: Feat480AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat480Event9(event: Feat480AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat480Event10(event: Feat480AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat480Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat480Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat480Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat480Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat480Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat480Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat480Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat480Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat480Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat480Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat480(u: CoreUser): Feat480Projection1 =
    Feat480Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat480Projection1> {
    val list = java.util.ArrayList<Feat480Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat480(u)
    }
    return list
}
