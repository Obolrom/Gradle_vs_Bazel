package com.romix.feature.feat596

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat596Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat596UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat596FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat596UserSummary
)

data class Feat596UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat596NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat596Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat596Config = Feat596Config()
) {

    fun loadSnapshot(userId: Long): Feat596NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat596NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat596UserSummary {
        return Feat596UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat596FeedItem> {
        val result = java.util.ArrayList<Feat596FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat596FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat596UiMapper {

    fun mapToUi(model: List<Feat596FeedItem>): Feat596UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat596UiModel(
            header = UiText("Feat596 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat596UiModel =
        Feat596UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat596UiModel =
        Feat596UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat596UiModel =
        Feat596UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat596Service(
    private val repository: Feat596Repository,
    private val uiMapper: Feat596UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat596UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat596UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat596UserItem1(val user: CoreUser, val label: String)
data class Feat596UserItem2(val user: CoreUser, val label: String)
data class Feat596UserItem3(val user: CoreUser, val label: String)
data class Feat596UserItem4(val user: CoreUser, val label: String)
data class Feat596UserItem5(val user: CoreUser, val label: String)
data class Feat596UserItem6(val user: CoreUser, val label: String)
data class Feat596UserItem7(val user: CoreUser, val label: String)
data class Feat596UserItem8(val user: CoreUser, val label: String)
data class Feat596UserItem9(val user: CoreUser, val label: String)
data class Feat596UserItem10(val user: CoreUser, val label: String)

data class Feat596StateBlock1(val state: Feat596UiModel, val checksum: Int)
data class Feat596StateBlock2(val state: Feat596UiModel, val checksum: Int)
data class Feat596StateBlock3(val state: Feat596UiModel, val checksum: Int)
data class Feat596StateBlock4(val state: Feat596UiModel, val checksum: Int)
data class Feat596StateBlock5(val state: Feat596UiModel, val checksum: Int)
data class Feat596StateBlock6(val state: Feat596UiModel, val checksum: Int)
data class Feat596StateBlock7(val state: Feat596UiModel, val checksum: Int)
data class Feat596StateBlock8(val state: Feat596UiModel, val checksum: Int)
data class Feat596StateBlock9(val state: Feat596UiModel, val checksum: Int)
data class Feat596StateBlock10(val state: Feat596UiModel, val checksum: Int)

fun buildFeat596UserItem(user: CoreUser, index: Int): Feat596UserItem1 {
    return Feat596UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat596StateBlock(model: Feat596UiModel): Feat596StateBlock1 {
    return Feat596StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat596UserSummary> {
    val list = java.util.ArrayList<Feat596UserSummary>(users.size)
    for (user in users) {
        list += Feat596UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat596UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat596UiModel {
    val summaries = (0 until count).map {
        Feat596UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat596UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat596UiModel> {
    val models = java.util.ArrayList<Feat596UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat596AnalyticsEvent1(val name: String, val value: String)
data class Feat596AnalyticsEvent2(val name: String, val value: String)
data class Feat596AnalyticsEvent3(val name: String, val value: String)
data class Feat596AnalyticsEvent4(val name: String, val value: String)
data class Feat596AnalyticsEvent5(val name: String, val value: String)
data class Feat596AnalyticsEvent6(val name: String, val value: String)
data class Feat596AnalyticsEvent7(val name: String, val value: String)
data class Feat596AnalyticsEvent8(val name: String, val value: String)
data class Feat596AnalyticsEvent9(val name: String, val value: String)
data class Feat596AnalyticsEvent10(val name: String, val value: String)

fun logFeat596Event1(event: Feat596AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat596Event2(event: Feat596AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat596Event3(event: Feat596AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat596Event4(event: Feat596AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat596Event5(event: Feat596AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat596Event6(event: Feat596AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat596Event7(event: Feat596AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat596Event8(event: Feat596AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat596Event9(event: Feat596AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat596Event10(event: Feat596AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat596Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat596Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat596Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat596Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat596Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat596Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat596Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat596Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat596Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat596Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat596(u: CoreUser): Feat596Projection1 =
    Feat596Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat596Projection1> {
    val list = java.util.ArrayList<Feat596Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat596(u)
    }
    return list
}
