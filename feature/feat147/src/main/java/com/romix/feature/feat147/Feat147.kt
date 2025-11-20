package com.romix.feature.feat147

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat147Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat147UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat147FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat147UserSummary
)

data class Feat147UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat147NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat147Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat147Config = Feat147Config()
) {

    fun loadSnapshot(userId: Long): Feat147NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat147NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat147UserSummary {
        return Feat147UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat147FeedItem> {
        val result = java.util.ArrayList<Feat147FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat147FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat147UiMapper {

    fun mapToUi(model: List<Feat147FeedItem>): Feat147UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat147UiModel(
            header = UiText("Feat147 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat147UiModel =
        Feat147UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat147UiModel =
        Feat147UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat147UiModel =
        Feat147UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat147Service(
    private val repository: Feat147Repository,
    private val uiMapper: Feat147UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat147UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat147UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat147UserItem1(val user: CoreUser, val label: String)
data class Feat147UserItem2(val user: CoreUser, val label: String)
data class Feat147UserItem3(val user: CoreUser, val label: String)
data class Feat147UserItem4(val user: CoreUser, val label: String)
data class Feat147UserItem5(val user: CoreUser, val label: String)
data class Feat147UserItem6(val user: CoreUser, val label: String)
data class Feat147UserItem7(val user: CoreUser, val label: String)
data class Feat147UserItem8(val user: CoreUser, val label: String)
data class Feat147UserItem9(val user: CoreUser, val label: String)
data class Feat147UserItem10(val user: CoreUser, val label: String)

data class Feat147StateBlock1(val state: Feat147UiModel, val checksum: Int)
data class Feat147StateBlock2(val state: Feat147UiModel, val checksum: Int)
data class Feat147StateBlock3(val state: Feat147UiModel, val checksum: Int)
data class Feat147StateBlock4(val state: Feat147UiModel, val checksum: Int)
data class Feat147StateBlock5(val state: Feat147UiModel, val checksum: Int)
data class Feat147StateBlock6(val state: Feat147UiModel, val checksum: Int)
data class Feat147StateBlock7(val state: Feat147UiModel, val checksum: Int)
data class Feat147StateBlock8(val state: Feat147UiModel, val checksum: Int)
data class Feat147StateBlock9(val state: Feat147UiModel, val checksum: Int)
data class Feat147StateBlock10(val state: Feat147UiModel, val checksum: Int)

fun buildFeat147UserItem(user: CoreUser, index: Int): Feat147UserItem1 {
    return Feat147UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat147StateBlock(model: Feat147UiModel): Feat147StateBlock1 {
    return Feat147StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat147UserSummary> {
    val list = java.util.ArrayList<Feat147UserSummary>(users.size)
    for (user in users) {
        list += Feat147UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat147UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat147UiModel {
    val summaries = (0 until count).map {
        Feat147UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat147UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat147UiModel> {
    val models = java.util.ArrayList<Feat147UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat147AnalyticsEvent1(val name: String, val value: String)
data class Feat147AnalyticsEvent2(val name: String, val value: String)
data class Feat147AnalyticsEvent3(val name: String, val value: String)
data class Feat147AnalyticsEvent4(val name: String, val value: String)
data class Feat147AnalyticsEvent5(val name: String, val value: String)
data class Feat147AnalyticsEvent6(val name: String, val value: String)
data class Feat147AnalyticsEvent7(val name: String, val value: String)
data class Feat147AnalyticsEvent8(val name: String, val value: String)
data class Feat147AnalyticsEvent9(val name: String, val value: String)
data class Feat147AnalyticsEvent10(val name: String, val value: String)

fun logFeat147Event1(event: Feat147AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat147Event2(event: Feat147AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat147Event3(event: Feat147AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat147Event4(event: Feat147AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat147Event5(event: Feat147AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat147Event6(event: Feat147AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat147Event7(event: Feat147AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat147Event8(event: Feat147AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat147Event9(event: Feat147AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat147Event10(event: Feat147AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat147Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat147Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat147Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat147Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat147Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat147Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat147Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat147Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat147Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat147Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat147(u: CoreUser): Feat147Projection1 =
    Feat147Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat147Projection1> {
    val list = java.util.ArrayList<Feat147Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat147(u)
    }
    return list
}
